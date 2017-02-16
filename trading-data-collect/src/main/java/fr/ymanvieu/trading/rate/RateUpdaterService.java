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
package fr.ymanvieu.trading.rate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@Service
public class RateUpdaterService {

	private LatestRateRepository latestRepo;
	
	private SymbolRepository symbolRepo;
	
	private RateService rateService;

	private ApplicationEventPublisher bus;

	@Autowired
	public RateUpdaterService(LatestRateRepository latestRepo, SymbolRepository symbolRepo, RateService rateService, ApplicationEventPublisher bus) {
		this.latestRepo = latestRepo;
		this.symbolRepo = symbolRepo;
		this.rateService = rateService;
		this.bus = bus;
	}

	/**
	 * Saves quotes only if they are newer than the existing corresponding rate
	 * (same from/tocur), if any : <br>
	 * "latesrates" table -> only if newer <br>
	 * "rates" table -> if doesn't exist
	 * <p>
	 * Quotes order doesn't matter.
	 * @see RateService#addHistoricalRates(List)
	 */
	@Transactional
	public void updateRates(List<Quote> quotes) {

		List<Quote> quotesList = new ArrayList<>(quotes);

		Collections.sort(quotesList, new Comparator<Quote>() {
			@Override
			public int compare(Quote o1, Quote o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});

		List<LatestRate> existingLatestRates = latestRepo.findAll();

		List<LatestRate> newLatestRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolRepo.findAll();

		for (Quote quote : quotesList) {
			LatestRate existingLatestRate = getFromList(existingLatestRates, quote);
			
			if (existingLatestRate == null) {
				
				//TODO skip and log if not exist
				getFromList(symbols, quote.getCurrency());
				getFromList(symbols, quote.getCode());

				LatestRate newLatestRate = new LatestRate(convertToRateEntity(quote, symbols));

				newLatestRates.add(newLatestRate);
				existingLatestRates.add(newLatestRate);

			} else if (quote.getTime().after(existingLatestRate.getDate())) {
				existingLatestRate.setDate(quote.getTime());
				existingLatestRate.setValue(quote.getPrice());

				if (!newLatestRates.contains(existingLatestRate)) {
					newLatestRates.add(existingLatestRate);
				}
			}
		}

		latestRepo.save(newLatestRates);

		// add all quotes (filtered in addHistoricalRates())
		rateService.addHistoricalRates(quotesList);

		if (!newLatestRates.isEmpty()) {
			bus.publishEvent(new RatesUpdatedEvent(newLatestRates));
		}
	}

	private <T extends RateEntity> T getFromList(List<T> ratesList, Quote q) {
		for (T r : ratesList) {
			if (r.getFromcur().getCode().equals(q.getCode())
					&& r.getTocur().getCode().equals(q.getCurrency())) {
				return r;
			}
		}

		return null;
	}

	private SymbolEntity getFromList(List<SymbolEntity> symbols, String code) {
		return symbols.stream().filter(s -> s.getCode().equals(code)).findFirst().orElse(null);
	}

	private RateEntity convertToRateEntity(Quote q, List<SymbolEntity> availableSymbols) {
		SymbolEntity fromcur = getFromList(availableSymbols, q.getCode());
		SymbolEntity tocur = getFromList(availableSymbols, q.getCurrency());

		return new RateEntity(fromcur, tocur, q.getPrice(), q.getTime());
	}
}