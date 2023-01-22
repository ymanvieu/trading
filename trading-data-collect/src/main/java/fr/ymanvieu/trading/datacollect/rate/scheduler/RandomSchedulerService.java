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
package fr.ymanvieu.trading.datacollect.rate.scheduler;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.datacollect.rate.RateUpdaterService;

/**
 * Fake data provider to simulate real-time data updates.
 */
@Service
@ConditionalOnProperty(name = "trading.scheduler.type", havingValue = "random")
public class RandomSchedulerService implements LatestRateProvider {

	private static final Random RANDOM = new Random();

	@Autowired
	private RateUpdaterService dataUpdater;

	@Scheduled(fixedRate = 5000)
	public void updateRates() throws IOException {
		dataUpdater.updateRates(this);
	}

	@Override
	public Quote getLatestRate(String code) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<Quote> getRates() {
		Quote usdeur = new Quote(USD, EUR, BigDecimal.valueOf(RANDOM.nextDouble()), Instant.now());
		Quote breusd = new Quote("BZ=F", USD, BigDecimal.valueOf(10 * RANDOM.nextDouble() + 30), Instant.now());

		return Arrays.asList(usdeur, breusd);
	}
}