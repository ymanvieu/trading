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
package fr.ymanvieu.trading.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;

import fr.ymanvieu.trading.provider.LookupDetails;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.LookupService;
import fr.ymanvieu.trading.provider.PairException;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.ProviderType;
import fr.ymanvieu.trading.provider.RateProviderService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.rate.Rate;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
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

		PairEntity pair = pairService.getForCodeAndProvider(code, provider);

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

		Optional<SymbolEntity> sourceSymbol = symbolService.getForCode(source);

		if(sourceSymbol.isPresent()) {
			if(sourceSymbol.get().getCurrency() == null) {
				throw AdminException.currencyAlreadyExists(source);
			}
		} else {
			symbolService.addSymbol(source, name, null, currency);
		}

		pair = pairService.create(code, name, source, currency, exchange, provider);

		final LatestRateProvider rProvider = rateProviderService.getLatestProvider(ProviderType.STOCK);

		Rate latestQuote = rProvider.getLatestRate(code);

		if (latestQuote == null) {
			throw SymbolException.UNAVAILABLE(code);
		}

		// TODO immutability ! copy data
		latestQuote.setCode(source);
		latestQuote.setCurrency(currency);

		List<Rate> historicalQuotes = new ArrayList<>();

		final HistoricalRateProvider hRProvider = rateProviderService.getHistoricalProvider(ProviderType.STOCK);

		try {
			historicalQuotes.addAll(hRProvider.getHistoricalRates(code));
		} catch (IOException | RuntimeException e) {
			// generally, if provider cannot get historical data, it throws exception
			log.warn("Cannot get historical data for: {} (provider: {})", code, provider);
		}

		for (Rate q : historicalQuotes) {
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

		PairEntity pair = pairService.getForCodeAndProvider(code, provider);

		if (pair == null) {
			throw SymbolException.UNKNOWN(code);
		}

		pairService.remove(pair);

		log.info("{} deleted in: {}", pair, sw);
	}
	
	public SearchResult search(String symbolOrName) throws IOException {
		final List<PairEntity> existingSymbols;
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

	private void removeDuplicates(List<LookupInfo> availableSymbols, List<PairEntity> existingSymbols) {
		availableSymbols.removeIf(as -> existingSymbols.stream().map(s -> s.getSymbol()).anyMatch(s -> s.equals(as.getCode())));
	}
}