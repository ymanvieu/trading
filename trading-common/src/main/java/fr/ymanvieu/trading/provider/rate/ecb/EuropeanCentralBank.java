/**
 * Copyright (C) 2014 Yoann Manvieu
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
package fr.ymanvieu.trading.provider.rate.ecb;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.MathUtils.invert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.ecb.Result.Node;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

/**
 * European Central Bank daily update.
 * <p>
 * Update occurs at 16:00 Europe/Brussels (data collected at 14:15) according to
 * the <a href=
 * "http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html" >ECB
 * website</a>.
 * 
 * @author ymanvieu
 * 
 */
@Component
public class EuropeanCentralBank implements HistoricalRateProvider {

	private static final String BASE_CURRENCY = CurrencyUtils.EUR;

	@Value("${provider.ecb.url.history}")
	private String urlHistory;

	private final RestTemplate rt = new RestTemplate();

	/**
	 * Returns rates with US dollar as base currency (for consistency with other
	 * data providers)
	 */
	@Override
	public List<Quote> getHistoricalRates() throws IOException {
		return getRates(rt.getForObject(urlHistory, Result.class), true);
	}

	@Override
	public List<Quote> getHistoricalRates(String code) throws IOException {
		throw new RuntimeException("not implemented");
	}

	private List<Quote> getRates(Result res, boolean usdBase) {
		List<Quote> rates = new ArrayList<>();

		for (Node day : res.getDays()) {
			Calendar cd = day.getTime();
			cd.setTimeZone(TimeZone.getTimeZone("Europe/Brussels"));
			cd.set(Calendar.HOUR_OF_DAY, 14);
			cd.set(Calendar.MINUTE, 15);
			Date date = cd.getTime();

			if (!usdBase) {
				for (Node rate : day.getRates()) {
					rates.add(new Quote(BASE_CURRENCY, rate.getCurrency(), rate.getRate(), date));
				}
			} else {
				BigDecimal usdToEurValue = null;

				for (Node rate : day.getRates()) {
					if (USD.equals(rate.getCurrency())) {
						usdToEurValue = invert(rate.getRate());
						break;
					}
				}

				for (Node rate : day.getRates()) {
					if (USD.equals(rate.getCurrency())) {
						rates.add(new Quote(USD, BASE_CURRENCY, usdToEurValue, date));
					} else {
						rates.add(new Quote(USD, rate.getCurrency(), rate.getRate().multiply(usdToEurValue), date));
					}
				}
			}
		}

		return rates;
	}
}
