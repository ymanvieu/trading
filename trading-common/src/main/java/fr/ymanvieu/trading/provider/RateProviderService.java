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
package fr.ymanvieu.trading.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.provider.rate.ecb.EuropeanCentralBank;
import fr.ymanvieu.trading.provider.rate.quandl.Quandl;
import fr.ymanvieu.trading.provider.rate.yahoo.Yahoo;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooStock;

@Service
public class RateProviderService {

	@Autowired
	private Yahoo yahoo;

	@Autowired
	private YahooStock yahooStock;

	@Autowired
	private Quandl quandl;

	@Autowired
	private EuropeanCentralBank ecb;

	public LatestRateProvider getProvider(ProviderType type) {
		LatestRateProvider provider = null;

		switch (type) {
			case FOREX:
				provider = yahoo;
			break;
			case OIL:
				provider = quandl;
			break;
			case STOCK:
				provider = yahooStock;
			break;
			default:
			break;
		}

		return provider;
	}

	public HistoricalRateProvider getHistoricalProvider(ProviderType type) {
		HistoricalRateProvider provider = null;

		switch (type) {
			case FOREX:
				provider = ecb;
			break;
			case OIL:
				provider = quandl;
			break;
			case STOCK:
			default:
		}

		return provider;
	}
}