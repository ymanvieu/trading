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

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "trading.scheduler.type", havingValue = "cron")
public class CronSchedulerService extends FixedRateSchedulerService {

	@Scheduled(zone = "UTC", cron = "${scheduler.interval.cron.forex}")
	@Override
	public void updateForex() throws IOException {
		super.updateForex();
	}

	@Scheduled(zone = "UTC", cron = "${scheduler.interval.cron.stock}")
	@Override
	public void updateStock() throws IOException {
		super.updateStock();
	}
}