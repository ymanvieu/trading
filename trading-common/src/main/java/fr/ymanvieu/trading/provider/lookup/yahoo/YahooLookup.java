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
package fr.ymanvieu.trading.provider.lookup.yahoo;

import static fr.ymanvieu.trading.util.StringUtils.format;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.common.annotations.VisibleForTesting;

import fr.ymanvieu.trading.provider.LookupDetails;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.lookup.LookupProvider;
import fr.ymanvieu.trading.provider.lookup.ProviderCode;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooModel;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

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

	private final RestTemplate rt = new RestTemplate();

	@Override
	public List<LookupInfo> search(String symbolOrName) throws IOException {
		YahooLookupModel result = rt.getForObject(format(url, symbolOrName), YahooLookupModel.class);

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
			currency = rt.getForObject(format(urlLatest, code), YahooModel.class).getQuoteResponse().getResult().get(0).getCurrency();
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
