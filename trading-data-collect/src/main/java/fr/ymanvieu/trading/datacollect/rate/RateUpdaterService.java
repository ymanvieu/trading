package fr.ymanvieu.trading.datacollect.rate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Stopwatch;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.rate.mapper.RateMapper;
import fr.ymanvieu.trading.common.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class RateUpdaterService {

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;

	@Autowired
	private ApplicationEventPublisher bus;
	
	@Autowired
	private RateMapper rateMapper;

	@Retryable
	public void updateRates(LatestRateProvider provider) throws IOException {

		String providerName = provider.getClass().getSimpleName();

		log.debug("{}: Updating rates", providerName);

		Stopwatch startWatch = Stopwatch.createStarted();

		List<Quote> quotes = provider.getRates();

		String downloadTimeFormatted =  startWatch.toString();

		if (quotes == null || quotes.isEmpty()) {
			log.info("{}: No rate to update", providerName);
			return;
		}

		Stopwatch saveWatch = Stopwatch.createStarted();

		updateRates(quotes);

		log.debug("{}: Rates stored in {}", providerName, saveWatch);
		log.info("{}: Update done in {} (download time: {})", providerName, startWatch, downloadTimeFormatted);
	}

	/**
	 * Saves quotes only if they are newer than the existing corresponding rate
	 * (same from/tocur), if any : <br>
	 * "latesrates" table -> only if newer <br>
	 * "rates" table -> if doesn't exist
	 * <p>
	 * Quotes order doesn't matter.
	 * 
	 * @see RateService#addHistoricalRates(List)
	 */
	public void updateRates(List<Quote> quotes) {

		List<Quote> quotesList = new ArrayList<>(quotes);

		quotesList.sort(Comparator.comparing(Quote::getTime));

		List<LatestRate> existingLatestRates = latestRepo.findAll();

		List<LatestRate> newLatestRates = new ArrayList<>();
		List<Quote> newHistoricalRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolRepo.findAll();

		for (Quote quote : quotesList) {
			LatestRate existingLatestRate = getFromList(existingLatestRates, quote);

			if (existingLatestRate == null) {

				SymbolEntity fromcur = getFromList(symbols, quote.getCode());
				
				if(fromcur == null) {
					log.warn("Cannot find symbol '{}' in DB, skipping it.", quote.getCode());
					continue;
				}

				SymbolEntity tocur = getFromList(symbols, quote.getCurrency());

				if(tocur == null) {
					log.warn("Cannot find symbol '{}' in DB, skipping it.", quote.getCurrency());
					continue;
				}
				
				LatestRate newLatestRate = new LatestRate(fromcur, tocur, quote.getPrice(), quote.getTime());

				newLatestRates.add(newLatestRate);
				existingLatestRates.add(newLatestRate);

			} else if (quote.getTime().isAfter(existingLatestRate.getDate())) {
				existingLatestRate.setDate(quote.getTime());
				existingLatestRate.setValue(quote.getPrice());

				if (!newLatestRates.contains(existingLatestRate)) {
					newLatestRates.add(existingLatestRate);
				}
			}
			
			newHistoricalRates.add(quote);
		}

		latestRepo.saveAll(newLatestRates);

		rateService.addHistoricalRates(newHistoricalRates);

		if (!newLatestRates.isEmpty()) {
			bus.publishEvent(new RatesUpdatedEvent().setRates(rateMapper.mapToRates(newLatestRates)));
		}
	}

	private LatestRate getFromList(List<LatestRate> ratesList, Quote q) {
		return ratesList.stream()
				.filter(r -> r.getFromcur().getCode().equals(q.getCode()) && r.getTocur().getCode().equals(q.getCurrency()))
				.findFirst().orElse(null);
	}

	private SymbolEntity getFromList(List<SymbolEntity> symbols, String code) {
		return symbols.stream().filter(s -> s.getCode().equals(code)).findFirst().orElse(null);
	}
}
