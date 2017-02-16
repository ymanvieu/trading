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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Stopwatch;

import fr.ymanvieu.trading.provider.LookupDetails;
import fr.ymanvieu.trading.provider.LookupService;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.ProviderException;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.StockService;
import fr.ymanvieu.trading.symbol.CurrencyInfo;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

@Service
@Transactional(readOnly = true)
public class AdminService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private SymbolService symbolService;

	private PairService pairService;

	private RateService rateService;

	private StockService stock;

	private LookupService lookupService;

	@Autowired
	public AdminService(SymbolService symbolService, PairService pairService, RateService rateService, StockService stock,
			LookupService lookupService) {
		this.symbolService = symbolService;
		this.pairService = pairService;
		this.rateService = rateService;
		this.stock = stock;
		this.lookupService = lookupService;
	}

	@Transactional(rollbackFor = Exception.class)
	public SymbolInfo add(String code, String provider) throws IOException, SymbolException, ProviderException {
		Stopwatch sw = Stopwatch.createStarted();

		boolean hasHistory = false;

		PairEntity pair = pairService.getForCode(code);

		if (pair != null) {
			throw SymbolException.ALREADY_EXISTS(pair.getSymbol());
		}

		LookupDetails details = lookupService.getDetails(code, provider);

		String source = details.getSource();
		String currency = details.getCurrency();
		String name = details.getName();

		// FIXME SEKAMD=X (armenian diram) O_o -> Advanced Micro...

		SymbolEntity targetSymbol = symbolService.getForCode(currency);

		if (targetSymbol == null) {
			// TODO refactor
			CurrencyInfo ci = symbolService.getCurrencyInfo(currency);
			String countryFlag = ci.getCountryFlag();
			String currencyName = ci.getName();

			currencyName = (countryFlag != null && currencyName != null) ? currencyName : name;

			symbolService.addSymbol(currency, currencyName, countryFlag, null);
		}

		SymbolEntity sourceSymbol = symbolService.getForCode(source);
		String sourceName = null;

		if (sourceSymbol == null) {
			// TODO refactor
			String countryFlag = CurrencyUtils.countryFlagForCurrency(source);
			String currencyName = CurrencyUtils.nameForCurrency(source);

			sourceName = (countryFlag != null && currencyName != null) ? currencyName : name;

			symbolService.addSymbol(source, sourceName, countryFlag, currency);
		}

		pair = pairService.create(code, name, source, currency, provider);

		Quote latestQuote = stock.getLatestRate(code);

		if (latestQuote == null) {
			throw SymbolException.UNAVAILABLE(code);
		}

		// TODO immutability ! copy data
		latestQuote.setCode(source);
		latestQuote.setCurrency(currency);

		List<Quote> historicalQuotes = new ArrayList<>();

		try {
			historicalQuotes.addAll(stock.getHistoricalRates(code));

			for (Quote q : historicalQuotes) {
				q.setCode(source);
				q.setCurrency(currency);
			}

			hasHistory = true;
		} catch (RuntimeException e) {
			log.warn("No history for code: {}", code);
		}

		historicalQuotes.add(latestQuote);

		rateService.addHistoricalRates(historicalQuotes);
		rateService.addLatestRate(latestQuote);

		log.info("{} created in: {}", pair.getSymbol(), sw);

		return new SymbolInfo(pair.getSymbol(), pair.getName(), hasHistory, latestQuote);
	}

	@Transactional(rollbackFor = SymbolException.class)
	public void delete(String code) throws SymbolException {
		Stopwatch sw = Stopwatch.createStarted();

		PairEntity pair = pairService.getForCode(code);

		if (pair == null) {
			throw SymbolException.UNKNOWN(code);
		}

		pairService.remove(code);

		log.info("{} deleted in: {}", pair.getSymbol(), sw);
	}
}