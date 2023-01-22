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
package fr.ymanvieu.trading.common.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooCurrencyProvider;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooStockProvider;

@Service
public class RateProviderService {

	@Autowired
	private YahooCurrencyProvider currencyProvider;

	@Autowired
	private YahooStockProvider stockProvider;

	public LatestRateProvider getLatestProvider(ProviderType type) {
		LatestRateProvider provider = null;

		switch (type) {
		case FOREX:
			provider = currencyProvider;
			break;
		case STOCK:
			provider = stockProvider;
			break;
		default:
			break;
		}

		return provider;
	}

	public HistoricalRateProvider getHistoricalProvider(ProviderType type) {
		HistoricalRateProvider provider = null;

		switch (type) {
		case STOCK:
			provider = stockProvider;
			break;
		default:
			break;
		}

		return provider;
	}
}