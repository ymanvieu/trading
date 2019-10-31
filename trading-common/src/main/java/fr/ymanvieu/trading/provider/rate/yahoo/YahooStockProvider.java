/**
 * Copyright (C) 2017 Yoann Manvieu
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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooHistoricalModel.Chart.Result;
import fr.ymanvieu.trading.rate.Rate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class YahooStockProvider implements LatestRateProvider, HistoricalRateProvider {

	private final RestTemplate rt = new RestTemplate();

	@Value("${provider.yahoo.url.latest}")
	private String url;

	@Value("${provider.yahoo.url.history}")
	private String urlHistory;

	@Autowired
	private PairService pairService;

	@Override
	public List<Rate> getRates() throws IOException {
		List<Rate> rates = new ArrayList<>();

		List<PairEntity> symbols = pairService.getAllFromProvider("YAHOO");

		if (symbols.isEmpty()) {
			log.trace("symbols list is empty");
			return rates;
		}

		YahooModel result = rt.getForObject(format(url, createPairsString(symbols)), YahooModel.class);

		return result.getQuoteResponse().getResult().stream()
				.map(yf -> new Rate(getSource(symbols, yf.getSymbol()), getCurrency(symbols, yf.getSymbol()),
						BigDecimal.valueOf(yf.getRegularMarketPrice()), toInstant(yf.getRegularMarketTime())))
				.collect(Collectors.toList());
	}
	
	private static Instant toInstant(long secondsTimestamp) {
		return Instant.ofEpochSecond(secondsTimestamp);
	}

	@Override
	public Rate getLatestRate(String symbol) {
		YahooModel result = rt.getForObject(format(url, symbol), YahooModel.class);

		return result
				.getQuoteResponse().getResult().stream().map(yf -> new Rate(yf.getSymbol(),
						BigDecimal.valueOf(yf.getRegularMarketPrice()), toInstant(yf.getRegularMarketTime())))
				.findFirst().orElse(null);
	}

	@Override
	public List<Rate> getHistoricalRates() throws IOException {
		List<Rate> quotes = new ArrayList<>();

		List<PairEntity> pairs = pairService.getAllFromProvider("YAHOO");

		for (PairEntity pair : pairs) {
			quotes.addAll(getHistoricalRates(pair.getSymbol()));
		}

		return quotes;
	}

	@Override
	public List<Rate> getHistoricalRates(String code) throws IOException {
		List<Result> results = rt.getForObject(format(urlHistory, code), YahooHistoricalModel.class).getChart()
				.getResult();

		List<Rate> rates = new ArrayList<>();

		if (results == null) {
			return rates;
		}

		Result result = results.get(0);

		String symbol = result.getMeta().getSymbol();
		String currency = result.getMeta().getCurrency();

		Instant firstTradeDate = Instant.ofEpochSecond(result.getMeta().getFirstTradeDate());

		Iterator<Long> itTimestamps = result.getTimestamp().iterator();
		Iterator<Double> itOpens = result.getIndicators().getQuote().get(0).getOpen().iterator();
		// Iterator<Double> itCloses = result.getIndicators().getQuote().get(0).getClose().iterator();

		rates.add(new Rate(symbol, currency, BigDecimal.valueOf(itOpens.next()), firstTradeDate));

		// skip first date (uses Result.Meta.FistTradeDate)
		itTimestamps.next();

		while (itTimestamps.hasNext()
				&& itOpens.hasNext() /* && itCloses.hasNext() */) {
			rates.add(new Rate(symbol, currency, BigDecimal.valueOf(itOpens.next()),
					toInstant(itTimestamps.next())));
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