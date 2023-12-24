package fr.ymanvieu.trading.datacollect.rate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import fr.ymanvieu.trading.common.provider.ProviderType;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.RateProviderService;
import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.rate.RateService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "trading.history.enabled", havingValue = "true")
public class HistoricalService implements ApplicationRunner {

	private final RateService rateService;
	private final RateProviderService providerService;

	public HistoricalService(RateService rateService, RateProviderService providerService) {
		this.rateService = rateService;
		this.providerService = providerService;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		addHistoricalRates(providerService.getHistoricalProvider(ProviderType.STOCK));
	}

	private void addHistoricalRates(HistoricalRateProvider provider) throws IOException {
		Stopwatch startWatch = Stopwatch.createStarted();
		
		String providerName = provider.getClass().getSimpleName();

		log.info("{}: Adding historical rates", providerName);

		List<Quote> quotes = provider.getHistoricalRates();

		Stopwatch saveWatch = Stopwatch.createStarted();

		addHistoricalRates(quotes);

		log.debug("{}: Data saved in {}", providerName, saveWatch);
		log.info("{}: Historical rates added in {}", providerName, startWatch);
	}

	@VisibleForTesting
	protected void addHistoricalRates(List<Quote> quotes) {

		log.debug("{} quotes as parameter", quotes.size());

		Table<String, String, List<Quote>> sortedQuotes = sort(quotes);

		sortedQuotes.cellSet().forEach(e -> {
			log.debug("===== {}/{} =====", e.getRowKey(), e.getColumnKey());
			log.debug("rates to add: {}", e.getValue().size());

			rateService.addHistoricalRates(e.getValue());
		});
	}

	private Table<String, String, List<Quote>> sort(List<Quote> quotes) {
		Table<String, String, List<Quote>> sortedQuotes = HashBasedTable.create();

		for (Quote q : quotes) {
			List<Quote> res = sortedQuotes.get(q.code(), q.currency());

			if (res == null) {
				res = new ArrayList<>();
				sortedQuotes.put(q.code(), q.currency(), res);
			}

			res.add(q);
		}

		return sortedQuotes;
	}
}
