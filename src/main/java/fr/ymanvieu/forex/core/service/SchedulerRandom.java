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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.provider.AProvider;

/**
 * Fake data provider to simulate real-time data updates in the historical chart web page.
 */
@Service
@ConditionalOnProperty("scheduler.random")
public class SchedulerRandom {

	private static final Random RANDOM = new Random();

	private final DataUpdater dataUpdater;

	@Autowired
	public SchedulerRandom(DataUpdater dataUpdater) {
		this.dataUpdater = dataUpdater;
	}

	@Scheduled(fixedRate = 5000)
	public void updateRates() throws IOException {
		dataUpdater.updateRates(new AProvider() {

			@Override
			public List<RateEntity> getRates() throws IOException {
				RateEntity usdeur = new RateEntity(USD, EUR, new BigDecimal(RANDOM.nextFloat()), new Date());
				RateEntity breusd = new RateEntity("BRE", USD, new BigDecimal(10 * RANDOM.nextFloat() + 30), new Date());

				return Arrays.asList(usdeur, breusd);
			}

			@Override
			public String toString() {
				return SchedulerRandom.class.getSimpleName();
			}
		});
	}
}