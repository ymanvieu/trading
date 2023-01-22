package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static fr.ymanvieu.trading.common.util.StringUtils.format;

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

import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.PairService;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel.Chart.Result;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class YahooStockProvider implements LatestRateProvider, HistoricalRateProvider {

	@Autowired
	private RestTemplate rt;

	@Value("${provider.yahoo.url.latest}")
	private String urlLatest;

	@Value("${provider.yahoo.url.history}")
	private String urlHistory;

	@Autowired
	private PairService pairService;

	@Override
	public List<Quote> getRates() throws IOException {
		List<Quote> rates = new ArrayList<>();

		List<Pair> pairs = pairService.getAllFromProvider("YAHOO");

		if (pairs.isEmpty()) {
			log.trace("pairs list is empty");
			return rates;
		}

		YahooModel result = rt.getForObject(format(urlLatest, createPairsString(pairs)), YahooModel.class);

		return result.getQuoteResponse().getResult().stream()
				.filter(r -> {
					if(r.getRegularMarketPrice() == null || r.getRegularMarketTime() == null) {
						log.warn("invalid result for: {}", r);
						return false;
					}
					return true;
				})
				.map(r -> new Quote(getSource(pairs, r.getSymbol()), getCurrency(pairs, r.getSymbol()),
						BigDecimal.valueOf(r.getRegularMarketPrice()), toInstant(r.getRegularMarketTime())))
				.collect(Collectors.toList());
	}
	
	private static Instant toInstant(long secondsTimestamp) {
		return Instant.ofEpochSecond(secondsTimestamp);
	}

	@Override
	public Quote getLatestRate(String symbol) {
		YahooModel result = rt.getForObject(format(urlLatest, symbol), YahooModel.class);

		return result
				.getQuoteResponse().getResult().stream().map(yf -> new Quote(yf.getSymbol(),
						BigDecimal.valueOf(yf.getRegularMarketPrice()), toInstant(yf.getRegularMarketTime())))
				.findFirst().orElse(null);
	}

	@Override
	public List<Quote> getHistoricalRates() throws IOException {
		List<Quote> quotes = new ArrayList<>();

		List<Pair> pairs = pairService.getAllFromProvider("YAHOO");

		for (Pair pair : pairs) {
			quotes.addAll(getHistoricalRates(pair.getSymbol()));
		}

		return quotes;
	}

	@Override
	public List<Quote> getHistoricalRates(String code) throws IOException {
		List<Result> results = rt.getForObject(format(urlHistory, code), YahooHistoricalModel.class).getChart()
				.getResult();

		List<Quote> rates = new ArrayList<>();

		if (results == null) {
			return rates;
		}

		Result result = results.get(0);

		String symbol = result.getMeta().getSymbol();
		String currency = result.getMeta().getCurrency();

		Instant firstTradeDate = Instant.ofEpochSecond(result.getMeta().getFirstTradeDate());

		Iterator<Long> itTimestamps = result.getTimestamp().iterator();
		Iterator<Double> itOpens = result.getIndicators().getQuote().get(0).getOpen().iterator();

		rates.add(new Quote(symbol, currency, BigDecimal.valueOf(itOpens.next()), firstTradeDate));

		// skip first date (uses Result.Meta.FistTradeDate)
		itTimestamps.next();

		while (itTimestamps.hasNext() && itOpens.hasNext()) {
			var price = itOpens.next();
			var time = itTimestamps.next();

			if (price != null && time != null) {
				rates.add(new Quote(symbol, currency, BigDecimal.valueOf(price), toInstant(time)));
			}
		}

		return rates;
	}

	private String getCurrency(List<Pair> symbols, String symbol) {
		for (Pair se : symbols) {
			if (se.getSymbol().equals(symbol)) {
				return se.getTarget().getCode();
			}
		}

		return null;
	}

	private String getSource(List<Pair> symbols, String symbol) {
		for (Pair se : symbols) {
			if (se.getSymbol().equals(symbol)) {
				return se.getSource().getCode();
			}
		}

		return null;
	}

	private String createPairsString(List<Pair> symbols) {
		return symbols.stream().map(Pair::getSymbol).collect(Collectors.joining(","));
	}
}
