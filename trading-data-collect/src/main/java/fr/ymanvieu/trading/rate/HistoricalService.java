/**
 * Copyright (C) 2014 Yoann Manvieu
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
package fr.ymanvieu.trading.rate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import fr.ymanvieu.trading.provider.ProviderType;
import fr.ymanvieu.trading.provider.RateProviderService;
import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.CurrencyInfo;
import fr.ymanvieu.trading.symbol.SymbolService;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@Service
@ConditionalOnProperty(name = "trading.history.enabled", havingValue = "true")
public class HistoricalService implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(HistoricalService.class);

	private RateService rateService;

	private RateProviderService providerService;

	private SymbolService symbolService;

	private SymbolRepository symbolRepo;
	
	@Autowired
	public HistoricalService(RateService rateService, RateProviderService providerService, SymbolService symbolService, SymbolRepository symbolRepo) {
		this.rateService = rateService;
		this.providerService = providerService;
		this.symbolService = symbolService;
		this.symbolRepo = symbolRepo;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		addHistoricalRates(providerService.getHistoricalProvider(ProviderType.OIL));
		addHistoricalRates(providerService.getHistoricalProvider(ProviderType.FOREX));
	}

	private void addHistoricalRates(HistoricalRateProvider provider) throws IOException {
		Stopwatch startWatch = Stopwatch.createStarted();

		log.info("{}: Adding historical rates", provider);

		List<Quote> quotes = provider.getHistoricalRates();

		Stopwatch saveWatch = Stopwatch.createStarted();

		addHistoricalRates(quotes);

		log.debug("{}: Data saved in {}", provider, saveWatch);
		log.info("{}: Historical rates added in {}", provider, startWatch);
	}

	@VisibleForTesting
	protected void addHistoricalRates(List<Quote> quotes) {

		log.debug("{} quotes as parameter", quotes.size());

		Table<String, String, List<Quote>> sortedQuotes = sort(quotes);

		sortedQuotes.cellSet().stream().forEach(e -> {

			if (!symbolRepo.exists(e.getColumnKey())) {
				addCurrency(e.getColumnKey());
			}
			
			if (!symbolRepo.exists(e.getRowKey())) {
				addCurrency(e.getRowKey());
			}

			log.debug("===== {}/{} =====", e.getRowKey(), e.getColumnKey());
			log.debug("rates to add: {}", e.getValue().size());

			rateService.addHistoricalRates(e.getValue());
		});
	}

	private Table<String, String, List<Quote>> sort(List<Quote> quotes) {
		Table<String, String, List<Quote>> sortedQuotes = HashBasedTable.create();

		for (Quote q : quotes) {
			List<Quote> res = sortedQuotes.get(q.getCode(), q.getCurrency());

			if (res == null) {
				res = new ArrayList<>();
				sortedQuotes.put(q.getCode(), q.getCurrency(), res);
			}

			res.add(q);
		}

		return sortedQuotes;
	}

	private void addCurrency(String code) {
		CurrencyInfo ci = symbolService.getCurrencyInfo(code);
		symbolService.addSymbol(ci.getCode(), ci.getName(), ci.getCountryFlag(), null);
	}
}