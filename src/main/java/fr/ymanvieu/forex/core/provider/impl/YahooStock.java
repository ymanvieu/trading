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
package fr.ymanvieu.forex.core.provider.impl;

import static fr.ymanvieu.forex.core.util.StringUtils.format;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;

import fr.ymanvieu.forex.core.model.Quote;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.SymbolRepository;
import fr.ymanvieu.forex.core.provider.AProvider;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooInfoModel;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooInfoModel.YahooResult;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel.YahooFields;
import fr.ymanvieu.forex.core.provider.impl.yahoo.YahooModel.YahooResource;

@Component
public class YahooStock extends AProvider {

	private static final Logger LOG = LoggerFactory.getLogger(YahooStock.class);

	private static final SimpleDateFormat HISTORY_DATE = new SimpleDateFormat("yyyy-MM-dd");

	@Value("${yahoo.url.latest}")
	private String url;

	@Value("${yahoo.url.info}")
	private String urlInfo;

	@Value("${yahoo.url.history}")
	private String urlHistory;

	@Autowired
	private SymbolRepository symbolRepo;

	private final ObjectMapper mapper = new ObjectMapper();

	public YahooStock() {
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	}

	@Override
	public List<RateEntity> getRates() throws IOException {
		List<RateEntity> rates = new ArrayList<>();

		List<SymbolEntity> symbols = symbolRepo.findAllByCurrencyNotNullOrderByCode();

		if (symbols.isEmpty()) {
			LOG.trace("symbols list is empty");
			return rates;
		}

		String response = sendGet(format(url, createSymbolsString(symbols)));

		YahooModel model = mapper.readValue(response, YahooModel.class);

		for (YahooResource o : model.getResources()) {

			YahooFields yf = o.getResource().getFields();

			rates.add(new RateEntity(yf.getSymbol(), getCurrency(symbols, yf.getSymbol()), yf.getPrice(), yf.getUtctime()));
		}

		return rates;
	}

	private String getCurrency(List<SymbolEntity> symbols, String symbol) {
		for (SymbolEntity se : symbols) {
			if (se.getCode().equals(symbol)) {
				return se.getCurrency().getCode();
			}
		}

		return null;
	}

	private String createSymbolsString(List<SymbolEntity> symbols) {
		StringBuilder sb = new StringBuilder();

		Iterator<SymbolEntity> it = symbols.iterator();
		while (it.hasNext()) {

			sb.append(it.next().getCode());

			if (it.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public List<Quote> getHistoricalRates(String code) throws IOException {
		String response = sendGet(format(urlHistory, code));

		List<String[]> csvLines;

		try (CSVReader reader = new CSVReader(new StringReader(response))) {
			csvLines = reader.readAll();
		}

		csvLines.remove(0);

		List<Quote> rates = new ArrayList<>();

		try {
			for (String[] line : csvLines) {
				// Date,Open,High,Low,Close,Volume,Adj Close
				// 2016-02-04,75.95,76.77,71.70,72.88,1782700,72.88

				Date date = HISTORY_DATE.parse(line[0]);
				BigDecimal close = new BigDecimal(line[4]);

				rates.add(new Quote(code, null, close, date));
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}

		return rates;
	}

	public String getCurrency(String code) throws IOException {
		String response = sendGet(format(urlInfo, code));

		YahooInfoModel model = mapper.readValue(response, YahooInfoModel.class);

		YahooResult o = model.getResults();

		return o.getQuote().getCurrency();
	}

	public Quote getLatestRate(String symbol) throws IOException {
		String response = sendGet(format(url, symbol));

		YahooModel model = mapper.readValue(response, YahooModel.class);

		YahooResource o = model.getResources().get(0);

		YahooFields yf = o.getResource().getFields();

		Quote quote = new Quote(yf.getSymbol(), yf.getIssuerName(), yf.getPrice(), yf.getUtctime());

		return quote;
	}
}
