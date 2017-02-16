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

import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.rate.Quote;

/**
 * Fake data provider to simulate real-time data updates.
 */
@Service
@ConditionalOnProperty(name = "trading.scheduler.type", havingValue = "random")
public class RandomSchedulerService extends SchedulerService implements LatestRateProvider {

	private static final Random RANDOM = new Random();
	
	@Autowired
	private SchedulerService service;

	@Scheduled(fixedRate = 5000)
	public void updateRates() throws IOException {
		service.updateRates(this);
	}

	@Override
	public void updateForex() throws IOException {
	}

	@Override
	public void updateOil() throws IOException {
	}

	@Override
	public void updateStock() throws IOException {
	}

	@Override
	public List<Quote> getRates() throws IOException {
		Quote usdeur = new Quote(USD, EUR, new BigDecimal(RANDOM.nextFloat()), new Date());
		Quote breusd = new Quote("BRE", USD, new BigDecimal(10 * RANDOM.nextFloat() + 30), new Date());
		Quote fakeeur = new Quote("FAKE", EUR, new BigDecimal(10 * RANDOM.nextFloat() + 30), new Date());

		return Arrays.asList(usdeur, breusd, fakeeur);
	}
}