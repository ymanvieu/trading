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
package fr.ymanvieu.forex.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.EventBus;

import fr.ymanvieu.forex.core.event.RatesUpdatedEvent;
import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.provider.AProvider;

@Service
public class DataUpdater {

	private static final Logger LOG = LoggerFactory.getLogger(DataUpdater.class);

	private final HistoricalRateRepository repo;

	private final LatestRateRepository latestrepo;

	private final SymbolService symbolService;

	private final EventBus bus;

	@Autowired
	public DataUpdater(HistoricalRateRepository repo, LatestRateRepository latestrepo, SymbolService symbolService, EventBus bus) {
		this.repo = repo;
		this.latestrepo = latestrepo;
		this.symbolService = symbolService;
		this.bus = bus;
	}

	public void updateRates(AProvider provider) throws IOException {

		Stopwatch startWatch = Stopwatch.createStarted();

		LOG.debug("{}: Updating rates", provider);

		List<RateEntity> rates = provider.getRates();

		if (rates == null || rates.isEmpty()) {
			LOG.info("{}: No rates to update", provider);
			return;
		}

		Collections.sort(rates, new Comparator<RateEntity>() {
			@Override
			public int compare(RateEntity o1, RateEntity o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
		});

		Stopwatch saveWatch = Stopwatch.createStarted();

		List<LatestRate> existingLatestRates = latestrepo.findAll();

		List<HistoricalRate> newHistoRates = new ArrayList<>();
		List<LatestRate> newLatestRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolService.getAll();

		for (RateEntity rate : rates) {
			LatestRate existingLatestRate = getFromList(existingLatestRates, rate);

			if (existingLatestRate == null) {
				// optimize
				addSymbol(symbols, rate.getFromcur().getCode());
				addSymbol(symbols, rate.getTocur().getCode());

				LatestRate newLatestRate = new LatestRate(rate);
				newLatestRates.add(newLatestRate);
				newHistoRates.add(new HistoricalRate(rate));
				existingLatestRates.add(newLatestRate);

			} else if (rate.getDate().after(existingLatestRate.getDate())) {
				existingLatestRate.setDate(rate.getDate());
				existingLatestRate.setValue(rate.getValue());

				if (!newLatestRates.contains(existingLatestRate)) {
					newLatestRates.add(existingLatestRate);
				}

				newHistoRates.add(new HistoricalRate(rate));
			}
		}

		latestrepo.save(newLatestRates);
		repo.save(newHistoRates);

		if (!newLatestRates.isEmpty()) {
			bus.post(new RatesUpdatedEvent(newLatestRates));
		}

		LOG.debug("{}: Data saved in {}", provider, saveWatch);
		LOG.info("{}: Rates updated in {}", provider, startWatch);
	}

	private void addSymbol(List<SymbolEntity> symbols, String code) {
		for (SymbolEntity se : symbols) {
			if (se.getCode().equals(code)) {
				return;
			}
		}

		symbols.add(symbolService.addSymbolForCurrency(code));
	}

	private <T extends RateEntity> T getFromList(List<T> rates, RateEntity rate) {
		for (T r : rates) {
			if (r.getFromcur().getCode().equals(rate.getFromcur().getCode()) && r.getTocur().getCode().equals(rate.getTocur().getCode())) {
				return r;
			}
		}

		return null;
	}
}