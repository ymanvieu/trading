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
package fr.ymanvieu.trading.provider.rate.yahoo;

import static fr.ymanvieu.trading.util.StringUtils.format;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooModel.YahooFields;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooModel.YahooResource;
import fr.ymanvieu.trading.rate.Quote;

@Component
public class YahooStock extends Yahoo implements HistoricalRateProvider {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Value("${provider.yahoo.url.latest}")
	private String url;

	@Value("${provider.yahoo.url.history}")
	private String urlHistory;

	@Autowired
	private PairService pairService;

	private String sendGetString(String url) {
		return rt.exchange(url, HttpMethod.GET, requestEntity(), String.class).getBody();
	}

	@Override
	public List<Quote> getRates() throws IOException {
		List<Quote> rates = new ArrayList<>();

		List<PairEntity> symbols = pairService.getAll();

		if (symbols.isEmpty()) {
			log.trace("symbols list is empty");
			return rates;
		}

		YahooModel model = sendGet(format(url, createPairsString(symbols)));

		for (YahooResource o : model.getList().getResources()) {

			YahooFields yf = o.getResource().getFields();

			rates.add(new Quote(getSource(symbols, yf.getSymbol()), getCurrency(symbols, yf.getSymbol()), yf.getPrice(), yf.getUtctime()));
		}

		return rates;
	}

	public Quote getLatestRate(String symbol) {
		YahooModel model = sendGet(format(url, symbol));

		YahooResource o = model.getList().getResources().get(0);

		YahooFields yf = o.getResource().getFields();

		Quote quote = new Quote(yf.getSymbol(), yf.getPrice(), yf.getUtctime());

		return quote;
	}

	@Override
	public List<Quote> getHistoricalRates() throws IOException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public List<Quote> getHistoricalRates(String code) throws IOException {
		String response = sendGetString(format(urlHistory, code));

		List<String[]> csvLines;

		try (CSVReader reader = new CSVReader(new StringReader(response))) {
			csvLines = reader.readAll();
		}

		csvLines.remove(0);

		List<Quote> rates = new ArrayList<>();

		DateFormat histoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			for (String[] line : csvLines) {
				// Date,Open,High,Low,Close,Volume,Adj Close
				// 2016-02-04,75.95,76.77,71.70,72.88,1782700,72.88

				Date date = histoDateFormat.parse(line[0]);
				BigDecimal close = new BigDecimal(line[4]);

				rates.add(new Quote(code, close, date));
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}

		return rates;
	}

	private String getCurrency(List<PairEntity> symbols, String symbol) {
		for (PairEntity se : symbols) {
			if (se.getSymbol().equals(symbol)) {
				return se.getTarget().getCode();
			}
		}

		return null;
	}

	private String getSource(List<PairEntity> symbols, String symbol) {
		for (PairEntity se : symbols) {
			if (se.getSymbol().equals(symbol)) {
				return se.getSource().getCode();
			}
		}

		return null;
	}

	private String createPairsString(List<PairEntity> symbols) {
		StringBuilder sb = new StringBuilder();

		Iterator<PairEntity> it = symbols.iterator();
		while (it.hasNext()) {

			sb.append(it.next().getSymbol());

			if (it.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}
}