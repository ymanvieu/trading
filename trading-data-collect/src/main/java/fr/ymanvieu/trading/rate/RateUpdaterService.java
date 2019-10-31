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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
	 * 
	 * @see RateService#addHistoricalRates(List)
	 */
	@Transactional
	public void updateRates(List<Rate> quotes) {

		List<Rate> quotesList = new ArrayList<>(quotes);

		Collections.sort(quotesList, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));

		List<LatestRate> existingLatestRates = latestRepo.findAll();

		List<LatestRate> newLatestRates = new ArrayList<>();
		List<Rate> newHistoricalRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolRepo.findAll();

		for (Rate quote : quotesList) {
			LatestRate existingLatestRate = getFromList(existingLatestRates, quote);

			if (existingLatestRate == null) {

				SymbolEntity fromcur = getFromList(symbols, quote.getCode());
				
				if(fromcur == null) {
					log.warn("Cannot find symbol '{}' in DB, skipping it.", quote.getCode());
					continue;
				}

				SymbolEntity tocur = getFromList(symbols, quote.getCurrency());

				if(tocur == null) {
					log.warn("Cannot find symbol '{}' in DB, skipping it.", quote.getCurrency());
					continue;
				}
				
				LatestRate newLatestRate = new LatestRate(fromcur, tocur, quote.getPrice(), quote.getTime());

				newLatestRates.add(newLatestRate);
				existingLatestRates.add(newLatestRate);

			} else if (quote.getTime().isAfter(existingLatestRate.getDate())) {
				existingLatestRate.setDate(quote.getTime());
				existingLatestRate.setValue(quote.getPrice());

				if (!newLatestRates.contains(existingLatestRate)) {
					newLatestRates.add(existingLatestRate);
				}
			}
			
			newHistoricalRates.add(quote);
		}

		latestRepo.saveAll(newLatestRates);

		rateService.addHistoricalRates(newHistoricalRates);

		if (!newLatestRates.isEmpty()) {
			bus.publishEvent(new RatesUpdatedEvent().setRates(newLatestRates));
		}
	}

	private LatestRate getFromList(List<LatestRate> ratesList, Rate q) {
		for (LatestRate r : ratesList) {
			if (r.getFromcur().getCode().equals(q.getCode()) && r.getTocur().getCode().equals(q.getCurrency())) {
				return r;
			}
		}

		return null;
	}

	private SymbolEntity getFromList(List<SymbolEntity> symbols, String code) {
		return symbols.stream().filter(s -> s.getCode().equals(code)).findFirst().orElse(null);
	}
}