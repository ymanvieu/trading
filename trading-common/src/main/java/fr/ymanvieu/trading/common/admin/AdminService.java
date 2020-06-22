/**
 * Copyright (C) 2016 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

	public SymbolInfo add(String code, String provider) throws IOException {
		Stopwatch sw = Stopwatch.createStarted();

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
		if (!symbolService.getForCodeWithNoCurrency(currency).isPresent()) {
			throw PairException.currencyNotFound(currency);
		}

		Optional<Symbol> sourceSymbol = symbolService.getForCode(source);

		if(sourceSymbol.isPresent()) {
			if(sourceSymbol.get().getCurrency() == null) {
				throw AdminException.currencyAlreadyExists(source);
			}
		} else {
			symbolService.addSymbol(source, name, null, currency);
		}

		pair = pairService.create(code, name, source, currency, exchange, provider);

		final LatestRateProvider rProvider = rateProviderService.getLatestProvider(ProviderType.STOCK);

		Quote latestQuote = rProvider.getLatestRate(code);

		if (latestQuote == null) {
			throw SymbolException.UNAVAILABLE(code);
		}

		// TODO immutability ! copy data
		latestQuote.setCode(source);
		latestQuote.setCurrency(currency);

		List<Quote> historicalQuotes = new ArrayList<>();

		final HistoricalRateProvider hRProvider = rateProviderService.getHistoricalProvider(ProviderType.STOCK);

		try {
			historicalQuotes.addAll(hRProvider.getHistoricalRates(code));
		} catch (IOException | RuntimeException e) {
			// generally, if provider cannot get historical data, it throws exception
			log.warn("Cannot get historical data for: {} (provider: {})", code, provider);
		}

		for (Quote q : historicalQuotes) {
			q.setCode(source);
			q.setCurrency(currency);
		}

		historicalQuotes.removeIf(q -> q.getTime().compareTo(latestQuote.getTime()) == 0);
		
		historicalQuotes.add(latestQuote);

		rateService.addHistoricalRates(historicalQuotes);
		rateService.addLatestRate(latestQuote);

		log.info("{} created in: {}", pair, sw);

		return new SymbolInfo(pair.getSymbol(), pair.getName(), !historicalQuotes.isEmpty(), latestQuote);
	}

	public void delete(String code, String provider) {
		Stopwatch sw = Stopwatch.createStarted();

		pairService.remove(code, provider);

		log.info("Pair [symbol: {}, provider: {}] deleted in: {}", code, provider, sw);
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
		availableSymbols.removeIf(as -> existingSymbols.stream().map(s -> s.getSymbol()).anyMatch(s -> s.equals(as.getCode())));
	}
}