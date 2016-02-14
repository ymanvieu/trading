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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty("scheduler.enabled")
public class Scheduler {

	private final DataUpdater dataUpdater;

	private final Oil oil;

	private final Forex forex;

	private final Stock stock;

	@Autowired
	public Scheduler(DataUpdater dataUpdater, Oil oil, Forex forex, Stock stock) {
		this.dataUpdater = dataUpdater;
		this.oil = oil;
		this.forex = forex;
		this.stock = stock;
	}

	@Scheduled(fixedRateString = "${scheduler.interval.forex}")
	public void updateForex() throws IOException {
		dataUpdater.updateRates(forex.getProvider());
	}

	@Scheduled(fixedRateString = "${scheduler.interval.oil}")
	public void updateOil() throws IOException {
		dataUpdater.updateRates(oil.getProvider());
	}

	@Scheduled(fixedRateString = "${scheduler.interval.stock}")
	public void updateStock() throws IOException {
		dataUpdater.updateRates(stock.getProvider());
	}
}