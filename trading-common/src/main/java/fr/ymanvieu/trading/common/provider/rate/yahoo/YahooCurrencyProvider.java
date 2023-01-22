package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.symbol.util.CurrencyUtils;

@Component
public class YahooCurrencyProvider implements LatestRateProvider {

	private static final Pattern FOREX_PATTERN = Pattern.compile("(\\w{3})(\\w{3})?=X");

	@Value("${provider.yahoo.url.latest.currencies}")
	private String urlLatest;

	@Autowired
	private RestTemplate rt;

	@Override
	public List<Quote> getRates() throws IOException {
		YahooModel result = rt.getForObject(urlLatest, YahooModel.class);

		return result.getQuoteResponse().getResult().stream()
				.map(yf -> {
					Matcher m = FOREX_PATTERN.matcher(yf.getSymbol());

					m.matches();
					String source = m.group(1);
					String target = m.group(2);

					if (target == null) {
						target = source;
						source = CurrencyUtils.USD;
					}

					Instant inst = Instant.ofEpochSecond(yf.getRegularMarketTime());
					
					return new Quote(source, target, BigDecimal.valueOf(yf.getRegularMarketPrice()), inst);
				})
				.collect(Collectors.toList());
	}
}
