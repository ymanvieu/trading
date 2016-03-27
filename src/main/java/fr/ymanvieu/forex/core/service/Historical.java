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
package fr.ymanvieu.forex.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;

import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.provider.AProvider;
import fr.ymanvieu.forex.core.provider.impl.EuropeanCentralBank;
import fr.ymanvieu.forex.core.provider.impl.Quandl;

@Service
@ConditionalOnProperty("history.enabled")
public class Historical {

	private static final Logger LOG = LoggerFactory.getLogger(Historical.class);

	private final HistoricalRateRepository repo;

	private final Quandl quandl;

	private final EuropeanCentralBank ecb;

	private final SymbolService symbolService;

	@Autowired
	public Historical(HistoricalRateRepository repo, Quandl quandl, EuropeanCentralBank ecb, SymbolService symbolService) {
		this.repo = repo;
		this.quandl = quandl;
		this.ecb = ecb;
		this.symbolService = symbolService;
	}

	@PostConstruct
	public void addHistory() throws IOException {
		process(quandl);
		process(ecb);
	}

	private void process(AProvider provider) throws IOException {
		Stopwatch startWatch = Stopwatch.createStarted();

		LOG.info("{}: Adding historical rates", provider);

		List<RateEntity> rates = provider.getHistoricalRates();

		Stopwatch saveWatch = Stopwatch.createStarted();

		List<HistoricalRate> newHistoRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolService.getAll();

		for (RateEntity r : rates) {
			// FIXME optimize
			addSymbol(symbols, r.getFromcur().getCode());
			addSymbol(symbols, r.getTocur().getCode());
			newHistoRates.add(new HistoricalRate(r));
		}

		repo.save(newHistoRates);

		LOG.debug("{}: Data saved in {}", provider, saveWatch);
		LOG.info("{}: Historical rates added in {}", provider, startWatch);
	}

	private void addSymbol(List<SymbolEntity> symbols, String code) {
		for (SymbolEntity se : symbols) {
			if (se.getCode().equals(code)) {
				return;
			}
		}

		symbols.add(symbolService.addSymbolForCurrency(code));
	}
}