package fr.ymanvieu.trading.common.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import fr.ymanvieu.trading.common.portofolio.PortofolioService;
import fr.ymanvieu.trading.common.portofolio.repository.AssetRepository;
import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;
import fr.ymanvieu.trading.common.provider.LookupService;
import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.PairException;
import fr.ymanvieu.trading.common.provider.PairService;
import fr.ymanvieu.trading.common.provider.ProviderType;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.RateProviderService;
import fr.ymanvieu.trading.common.provider.UpdatedPair;
import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.common.symbol.SymbolException;
import fr.ymanvieu.trading.common.symbol.SymbolService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class AdminService {

	@Autowired
	private SymbolService symbolService;

	@Autowired
	private PairService pairService;

	@Autowired
	private RateService rateService;

	@Autowired
	private RateProviderService rateProviderService;

	@Autowired
	private LookupService lookupService;

	@Autowired
	private AssetRepository assetRepository;

	@Autowired
	private PortofolioService portofolioService;

	public PairInfo add(String code, String provider) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();

		var pair = createPair(code, provider);

		final LatestRateProvider rProvider = rateProviderService.getLatestProvider(ProviderType.STOCK);

		Quote latestQuote = rProvider.getLatestRate(code);

		if (latestQuote == null) {
			throw SymbolException.UNAVAILABLE(code);
		}

		// TODO immutability ! copy data
		latestQuote.setCode(pair.getSource().getCode());
		latestQuote.setCurrency(pair.getTarget().getCode());

		List<Quote> historicalQuotes = new ArrayList<>();

		final HistoricalRateProvider hRProvider = rateProviderService.getHistoricalProvider(ProviderType.STOCK);

		try {
			historicalQuotes.addAll(hRProvider.getHistoricalRates(code));
		} catch (IOException | RuntimeException e) {
			// generally, if provider cannot get historical data, it throws exception
			log.warn("Cannot get historical data for: {} (provider: {})", code, provider, e);
		}

		for (Quote q : historicalQuotes) {
			q.setCode(pair.getSource().getCode());
			q.setCurrency(pair.getTarget().getCode());
		}

		historicalQuotes.removeIf(q -> q.getTime().compareTo(latestQuote.getTime()) == 0);

		historicalQuotes.add(latestQuote);

		rateService.addHistoricalRates(historicalQuotes);
		rateService.addLatestRate(latestQuote);

		log.info("{} created in: {}", pair, sw);

		return new PairInfo(pair.getId(), pair.getSymbol(), pair.getName(), latestQuote);
	}

	private Pair createPair(String code, String provider) throws IOException {

		Pair pair = pairService.getForCodeAndProvider(code, provider);

		if (pair != null) {
			throw PairException.alreadyExists(pair.getSymbol(), provider);
		}

		LookupDetails details = lookupService.getDetails(code, provider);

		String source = details.getSource();
		String currency = details.getCurrency();
		String name = details.getName();
		String exchange = details.getExchange();

		// check if currency exists
		if (symbolService.getForCodeWithNoCurrency(currency).isEmpty()) {
			throw PairException.currencyNotFound(currency);
		}

		Optional<Symbol> existingSourceSymbol = symbolService.getForCode(source);

		if(existingSourceSymbol.isPresent()) {
			if(existingSourceSymbol.get().getCurrency() == null) {
				throw AdminException.currencyAlreadyExists(source);
			} else if (!existingSourceSymbol.get().getCurrency().getCode().equals(currency)) {
				throw AdminException.alreadyExistsWithOtherCurrency(source, existingSourceSymbol.get().getCurrency().getCode());
			}
		} else {
			symbolService.addSymbol(source, name, null, currency);
		}

		return pairService.create(code, name, source, currency, exchange, provider);
	}

	public void delete(Integer pairId, boolean withSymbol) {
		Stopwatch sw = Stopwatch.createStarted();

		var deletedPair = pairService.remove(pairId);

        if (withSymbol) {
            assetRepository.findAllBySymbolCodeAndCurrencyCode(deletedPair.getSource().getCode(), deletedPair.getTarget().getCode())
                .forEach(asset -> {
                    portofolioService.sell(asset.getPortofolio().getUser().getId(), asset.getSymbol().getCode(), asset.getQuantity().doubleValue());
                });

            rateService.deleteRates(deletedPair.getSource().getCode(), deletedPair.getTarget().getCode());

			if (!pairService.existsAsSource(deletedPair.getSource().getCode())) {
				symbolService.delete(deletedPair.getSource().getCode());
			}
        }

		log.info("Pair [symbol: {}, provider: {}] deleted in: {}", deletedPair.getSymbol(), deletedPair.getProviderCode(), sw);
	}

	public SearchResult search(String symbolOrName) throws IOException {
		final List<UpdatedPair> existingSymbols;
		final List<LookupInfo> availableSymbols;

		if (Strings.isNullOrEmpty(symbolOrName)) {
			existingSymbols = pairService.getAll();
			availableSymbols = new ArrayList<>();
		} else {
			existingSymbols = pairService.getAllWithSymbolOrNameContaining(symbolOrName);
			availableSymbols = lookupService.search(symbolOrName);
			removeDuplicates(availableSymbols, existingSymbols);
		}

		return new SearchResult(existingSymbols, availableSymbols);
	}

	private void removeDuplicates(List<LookupInfo> availableSymbols, List<UpdatedPair> existingSymbols) {
		availableSymbols.removeIf(as -> existingSymbols.stream().map(UpdatedPair::getSymbol).anyMatch(s -> s.equals(as.getCode())));
	}

	public PairInfo update(UpdatedPair pair, Long connectedUserId) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();

		var existingPair = pairService.getForId(pair.getId());

		if (pair.getSymbol().equals(existingPair.getSymbol())) {
			throw new IllegalArgumentException("Updating same pair code: " + pair.getSymbol());
		}

		var createdPair = createPair(pair.getSymbol(), existingPair.getProviderCode());
		var newSymbol = new Symbol(createdPair.getSource().getCode(), null, null, createdPair.getTarget());

		rateService.updateRates(existingPair, newSymbol);
		assetRepository.update(existingPair, newSymbol, String.valueOf(connectedUserId));

		delete(pair.getId(), true);

		log.info("Pair [symbol: {}, provider: {}] updated in: {}", pair.getSymbol(), pair.getProviderCode(), sw);

		return new PairInfo(createdPair.getId(), createdPair.getSymbol(), createdPair.getName(), null);
	}
}
