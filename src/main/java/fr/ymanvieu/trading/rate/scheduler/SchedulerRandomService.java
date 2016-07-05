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
package fr.ymanvieu.trading.rate.scheduler;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;

/**
 * Fake data provider to simulate real-time data updates in the historical chart
 * web page.
 */
@Service
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "random")
public class SchedulerRandomService {

	private static final Random RANDOM = new Random();

	@Autowired
	private RateService dataUpdater;

	@Scheduled(fixedRate = 5000)
	public void updateRates() {
		Quote usdeur = new Quote(USD, EUR, new BigDecimal(RANDOM.nextFloat()), new Date());
		Quote breusd = new Quote("BRE", USD, new BigDecimal(10 * RANDOM.nextFloat() + 30), new Date());

		dataUpdater.updateRates(Arrays.asList(usdeur, breusd));
	}
}