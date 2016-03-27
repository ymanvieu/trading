/**
 * Copyright (C) 2016 Yoann Manvieu <ymanvieu@gmail.com>
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.forex.core.model.Quote;
import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.model.repositories.SymbolRepository;
import fr.ymanvieu.forex.core.util.CurrencyUtils;

@Service
public class SymbolService {

	private final LatestRateRepository latestrepo;

	private final HistoricalRateRepository repo;

	private final SymbolRepository symbolRepo;

	@Autowired
	public SymbolService(LatestRateRepository latestrepo, HistoricalRateRepository repo, SymbolRepository symbolRepo) {
		this.latestrepo = latestrepo;
		this.repo = repo;
		this.symbolRepo = symbolRepo;
	}

	public SymbolEntity addSymbolForCurrency(String code) {
		Objects.requireNonNull(code, "code is null");

		SymbolEntity se = symbolRepo.findByCode(code);

		if (se == null) {
			se = new SymbolEntity(code);
			se.setName(CurrencyUtils.nameForCurrency(code));
			se.setCountryFlag(CurrencyUtils.countryFlagForCurrency(code));
			se = symbolRepo.save(se);
		}

		return se;
	}

	@Transactional
	public SymbolEntity addSymbol(Quote latestQuote, List<Quote> historicalQuotes) {
		SymbolEntity se;
		SymbolEntity seCurrency = symbolRepo.findByCode(latestQuote.getCurrency());

		if (seCurrency == null) {
			seCurrency = addSymbolForCurrency(latestQuote.getCurrency());
		}

		se = new SymbolEntity(latestQuote.getCode());
		se.setName(latestQuote.getName());
		se.setCurrency(seCurrency);

		se = symbolRepo.save(se);

		List<HistoricalRate> hRates = new ArrayList<>();

		// add historical data
		for (Quote hQuote : historicalQuotes) {
			hRates.add(new HistoricalRate(new RateEntity(se, seCurrency, hQuote.getPrice(), hQuote.getTime())));
		}

		RateEntity latestRate = new RateEntity(se, seCurrency, latestQuote.getPrice(), latestQuote.getTime());

		// add latest data
		hRates.add(new HistoricalRate(latestRate));

		latestrepo.save(new LatestRate(latestRate));
		repo.save(hRates);
		return se;
	}

	@Transactional
	public void removeSymbol(String code) {
		// FIXME maybe use cascading ?
		latestrepo.deleteByFromcurCodeOrTocurCode(code);
		repo.deleteByFromcurCodeOrTocurCode(code);
		symbolRepo.deleteByCode(code);
	}

	public List<SymbolEntity> getAllWithCurrency() {
		return symbolRepo.findAllByCurrencyNotNullOrderByCode();
	}

	public List<SymbolEntity> getAllWithCurrency(String currency) {
		return symbolRepo.findAllByCurrencyCode(currency);
	}

	public SymbolEntity getForCode(String code) {
		return symbolRepo.findByCode(code);
	}

	public List<SymbolEntity> getAll() {
		return symbolRepo.findAll();
	}
}
