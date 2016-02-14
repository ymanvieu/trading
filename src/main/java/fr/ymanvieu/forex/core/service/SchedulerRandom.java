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

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.mysema.query.BooleanBuilder;

import fr.ymanvieu.forex.core.event.RatesUpdatedEvent;
import fr.ymanvieu.forex.core.model.entity.rate.HistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.QLatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;

/**
 * Fake data provider to simulate real-time data updates in the historical chart web page.
 */
@Service
@ConditionalOnProperty("scheduler.random")
public class SchedulerRandom {

	private static final Random RANDOM = new Random();

	@Autowired
	private LatestRateRepository lastRepo;

	@Autowired
	private HistoricalRateRepository repo;

	private static final Logger LOG = LoggerFactory.getLogger(SchedulerRandom.class);

	@Autowired
	private EventBus bus;

	@Scheduled(fixedRate = 5000)
	public void updateRates() {
		RateEntity rate = new RateEntity(USD, EUR, new BigDecimal(RANDOM.nextFloat()), new Date());
		HistoricalRate hRate = new HistoricalRate(rate);
		repo.save(hRate);

		QLatestRate rate1 = QLatestRate.latestRate;

		LatestRate lrate = lastRepo.findOne(new BooleanBuilder(rate1.fromcur.startsWith(USD)).and(rate1.tocur.startsWith(EUR)));

		if (lrate == null) {
			lrate = new LatestRate(rate);
		} else {
			lrate.setDate(rate.getDate());
			lrate.setValue(rate.getValue());
		}

		lastRepo.save(lrate);

		bus.post(new RatesUpdatedEvent(Lists.newArrayList(rate)));
		LOG.debug("Updated: new rate is {}", rate);
	}
}