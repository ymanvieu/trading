package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static java.math.BigDecimal.valueOf;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel.Chart.Result;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel.Chart.Result.Meta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
abstract class AbstractYahooProvider {

    @Autowired
    private RestTemplate rt;

    @Value("${provider.yahoo.url.history}")
    private String urlHistory;

    private final Function<Meta, Quote> RESULT_TO_QUOTE = result -> {
        var source = YahooSymbolParser.parseSource(result.getSymbol());
        var target = result.getCurrency().toUpperCase();

        return new Quote(source, target, valueOf(result.getRegularMarketPrice()), toInstant(result.getRegularMarketTime()));
    };

    public List<Quote> getRates(List<String> symbols) {
        List<Quote> rates = new ArrayList<>();

        if (symbols.isEmpty()) {
            log.trace("symbols list is empty");
            return rates;
        }

        return symbols.stream().parallel().map(symbol -> {

            YahooHistoricalModel response;
            try {
                response = rt.getForObject(urlHistory, YahooHistoricalModel.class, symbol, "1d");
            } catch (HttpStatusCodeException ex) {
                log.warn("Response for {}: {}", symbol, ex.getMessage());
                return null;
            }

            return response.getChart().getResult().stream().map(Result::getMeta)
                .filter(r -> {
                    if (r.getRegularMarketPrice() == null || r.getRegularMarketTime() == null) {
                        log.warn("invalid result for: {}", r);
                        return false;
                    }
                    return true;
                })
                .findFirst()
                .map(RESULT_TO_QUOTE)
                .orElseThrow(() -> new RuntimeException("no result for " + symbol));
        }).filter(Objects::nonNull).toList();
    }

    private static Instant toInstant(long secondsTimestamp) {
        return Instant.ofEpochSecond(secondsTimestamp);
    }

    public Quote getLatestRate(String symbol) {
        var result = rt.getForObject(urlHistory, YahooHistoricalModel.class, symbol, "1d");

        return result.getChart().getResult().stream().map(Result::getMeta).findFirst().map(RESULT_TO_QUOTE).orElse(null);
    }

    public List<Quote> getHistoricalRates(String symbol) {
        List<Result> results = rt.getForObject(urlHistory, YahooHistoricalModel.class, symbol, "max").getChart()
            .getResult();

        List<Quote> rates = new ArrayList<>();

        if (results == null) {
            return rates;
        }

        Result result = results.get(0);

        String code = YahooSymbolParser.parseSource(result.getMeta().getSymbol());
        String currency = result.getMeta().getCurrency().toUpperCase();

        Instant firstTradeDate = Instant.ofEpochSecond(result.getMeta().getFirstTradeDate());

        Iterator<Long> itTimestamps = result.getTimestamp().iterator();
        Iterator<Double> itOpens = result.getIndicators().getQuote().get(0).getOpen().iterator();

        rates.add(new Quote(code, currency, valueOf(itOpens.next()), firstTradeDate));

        // skip first date (uses Result.Meta.FistTradeDate)
        itTimestamps.next();

        while (itTimestamps.hasNext() && itOpens.hasNext()) {
            var price = itOpens.next();
            var time = itTimestamps.next();

            if (price != null && time != null) {
                rates.add(new Quote(code, currency, valueOf(price), toInstant(time)));
            }
        }

        return rates;
    }
}
