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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;

import fr.ymanvieu.trading.provider.RateProviderService;
import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.provider.ProviderType;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;

@Service
@ConditionalOnProperty(name = "scheduler.enabled", havingValue = "fixed-rate")
public class SchedulerService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RateService dataUpdater;

	@Autowired
	private RateProviderService dataProvider;

	@Scheduled(fixedRateString = "${scheduler.interval.fixed-rate.forex}")
	public void updateForex() throws IOException {
		updateRates(ProviderType.FOREX);
	}

	@Scheduled(fixedRateString = "${scheduler.interval.fixed-rate.oil}")
	public void updateOil() throws IOException {
		updateRates(ProviderType.OIL);
	}

	@Scheduled(fixedRateString = "${scheduler.interval.fixed-rate.stock}")
	public void updateStock() throws IOException {
		updateRates(ProviderType.STOCK);
	}

	private void updateRates(ProviderType type) throws IOException {
		LatestRateProvider provider = dataProvider.getProvider(type);

		log.debug("{}: Updating rates", provider);

		Stopwatch startWatch = Stopwatch.createStarted();

		List<Quote> quotes = provider.getRates();

		Stopwatch saveWatch = Stopwatch.createStarted();

		dataUpdater.updateRates(quotes);

		log.debug("{}: Rates stored in {}", provider, saveWatch);
		log.info("{}: Update done in {}", provider, startWatch);
	}
}