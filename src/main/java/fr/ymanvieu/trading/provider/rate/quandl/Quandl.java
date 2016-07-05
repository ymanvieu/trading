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
package fr.ymanvieu.trading.provider.rate.quandl;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.provider.rate.AProvider;
import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.rate.Quote;

@Component
public class Quandl extends AProvider implements LatestRateProvider, HistoricalRateProvider {

	private static final Logger LOG = LoggerFactory.getLogger(Quandl.class);

	public static final String BRE = "BRE";

	@Value("${quandl.url.history}")
	private String historyUrl;

	@Value("${quandl.url.latest}")
	private String latestUrl;

	private final ObjectMapper mapper = new ObjectMapper();

	public Quandl() {
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	}

	@Override
	public List<Quote> getRates() throws IOException {
		return getRates(latestUrl);
	}

	@Override
	public List<Quote> getHistoricalRates() throws IOException {
		return getRates(historyUrl);
	}

	@Override
	public List<Quote> getHistoricalRates(String code) throws IOException {
		throw new RuntimeException("not implemented");
	}

	private List<Quote> getRates(String url) throws IOException {

		QuandlModel model = mapper.readValue(sendGet(url), QuandlModel.class);

		List<Quote> rates = new ArrayList<>();

		List<QuandlModel.Rate> quandlRates = model.getData();

		for (QuandlModel.Rate o : quandlRates) {
			if (o.getValue() == null) {
				LOG.warn("No data for date: {}", o.getDate());
			} else {
				Calendar c = Calendar.getInstance();
				c.setTime(o.getDate());
				c.add(Calendar.DATE, 1);
				rates.add(new Quote(BRE, USD, o.getValue(), c.getTime()));
			}
		}

		return rates;
	}
}