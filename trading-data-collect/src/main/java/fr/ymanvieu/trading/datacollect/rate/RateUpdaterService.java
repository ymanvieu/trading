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
package fr.ymanvieu.trading.datacollect.rate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;
import fr.ymanvieu.trading.common.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.common.rate.mapper.RateMapper;
import fr.ymanvieu.trading.common.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RateUpdaterService {

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;

	@Autowired
	private ApplicationEventPublisher bus;
	
	@Autowired
	private RateMapper rateMapper;

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
	public void updateRates(List<Quote> quotes) {

		List<Quote> quotesList = new ArrayList<>(quotes);

		Collections.sort(quotesList, (o1, o2) -> o1.getTime().compareTo(o2.getTime()));

		List<LatestRate> existingLatestRates = latestRepo.findAll();

		List<LatestRate> newLatestRates = new ArrayList<>();
		List<Quote> newHistoricalRates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolRepo.findAll();

		for (Quote quote : quotesList) {
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
			bus.publishEvent(new RatesUpdatedEvent().setRates(rateMapper.mapToRates(newLatestRates)));
		}
	}

	private LatestRate getFromList(List<LatestRate> ratesList, Quote q) {
		return ratesList.stream()
				.filter(r -> r.getFromcur().getCode().equals(q.getCode()) && r.getTocur().getCode().equals(q.getCurrency()))
				.findFirst().orElse(null);
	}

	private SymbolEntity getFromList(List<SymbolEntity> symbols, String code) {
		return symbols.stream().filter(s -> s.getCode().equals(code)).findFirst().orElse(null);
	}
}