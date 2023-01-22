package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import static fr.ymanvieu.trading.common.util.StringUtils.format;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.google.common.annotations.VisibleForTesting;

import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;
import fr.ymanvieu.trading.common.provider.lookup.LookupProvider;
import fr.ymanvieu.trading.common.provider.lookup.ProviderCode;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooModel;
import fr.ymanvieu.trading.common.symbol.util.CurrencyUtils;

@Component
public class YahooLookup implements LookupProvider {

	private static final Pattern EQUITY_PATTERN = Pattern.compile("([\\w_-]+)[\\.[\\w]*]*");
	private static final Pattern FOREX_PATTERN = Pattern.compile("(\\w{3})(\\w{3})?=X");
	private static final Pattern FUTURE_PATTERN = Pattern.compile("(\\w+=F)");
	private static final Pattern CRYPTOCURRENCY_PATTERN = Pattern.compile("([\\w]{3,4})-([\\w]{3,4})");

	@Value("${provider.yahoo.url.lookup}")
	private String url;

	@Value("${provider.yahoo.url.latest}")
	private String urlLatest;

	@Autowired
	private RestTemplate rt;

	@Override
	public List<LookupInfo> search(String symbolOrName) throws IOException {
		YahooLookupModel result = rt.getForObject(format(url, symbolOrName), YahooLookupModel.class);

		if (result == null) {
			return List.of();
		}

		return result.getItems().stream()
		.map(r -> new LookupInfo(r.getSymbol(), r.getName(), r.getExchDisp(), r.getTypeDisp(), getProviderCode()))
		.collect(Collectors.toList());
	}

	
	@Override
	public LookupDetails getDetails(String code) throws IOException {
		List<LookupInfo> result = search(code);

		LookupInfo cl = result.stream().filter(li -> li.getCode().equals(code)).findFirst().orElse(null);

		String name = cl.getName();
		String source = parseSource(code);
		String currency = parseTarget(code);

		if (currency == null) {
			currency = rt.getForObject(format(urlLatest, code), YahooModel.class).getQuoteResponse().getResult().get(0).getCurrency().toUpperCase();
		}

		return new LookupDetails(code, name, source, currency, cl.getExchange(), getProviderCode());
	}

	@VisibleForTesting
	protected static String parseSource(String code) {
		Matcher forexMatcher = FOREX_PATTERN.matcher(code);
		
		if (forexMatcher.matches()) {
			String source = forexMatcher.group(1);
			String target = forexMatcher.group(2);
			return (target != null) ? source : CurrencyUtils.USD;
		}
		
		Matcher cryptoMatcher = CRYPTOCURRENCY_PATTERN.matcher(code);
		
		if (cryptoMatcher.matches()) {
			return cryptoMatcher.group(1);
		}
		
		Matcher stockMatcher = EQUITY_PATTERN.matcher(code);

		if (stockMatcher.matches()) {
			return stockMatcher.group(1);
		}
		
		Matcher futureMatcher = FUTURE_PATTERN.matcher(code);

		if (futureMatcher.matches()) {
			return futureMatcher.group(1);
		}

		return null;
	}

	@VisibleForTesting
	protected static String parseTarget(String code) {
		Matcher forexMatcher = FOREX_PATTERN.matcher(code);

		if (forexMatcher.matches()) {
			String source = forexMatcher.group(1);
			String target = forexMatcher.group(2);
			return (target != null) ? target : source;
		}
		
		Matcher cryptoMatcher = CRYPTOCURRENCY_PATTERN.matcher(code);

		if (cryptoMatcher.matches()) {
			return cryptoMatcher.group(2);
		}

		return null;
	}

	@Override
	public String getProviderCode() {
		return ProviderCode.YAHOO.name();
	}
}
