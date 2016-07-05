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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ymanvieu.trading.http.ConnectionHandler;
import fr.ymanvieu.trading.provider.LookupDetails;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.lookup.LookupProvider;
import fr.ymanvieu.trading.provider.lookup.ProviderCode;
import fr.ymanvieu.trading.provider.lookup.yahoo.YahooInfoModel.YahooResult;
import fr.ymanvieu.trading.provider.lookup.yahoo.YahooLookupModel.YahooCompanyLookupResult;
import fr.ymanvieu.trading.provider.rate.yahoo.Yahoo;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.util.StringUtils;

@Component
public class YahooLookup implements LookupProvider {

	@Autowired
	private ConnectionHandler handler;

	@Value("${yahoo.url.company-lookup}")
	private String url;

	@Value("${yahoo.url.info}")
	private String urlInfo;

	private final ObjectMapper mapper = new ObjectMapper();

	private static final Pattern STOCK_PATTERN = Pattern.compile("(\\w+)[\\.[\\w]*]*");

	public YahooLookup() {
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	}

	@Override
	public List<LookupInfo> search(String symbolOrName) throws IOException {
		String response = handler.sendGet(format(url, symbolOrName));
		List<LookupInfo> results = new ArrayList<>();

		List<YahooCompanyLookupResult> result = mapper.readValue(response, YahooLookupModel.class).getResult();

		for (YahooCompanyLookupResult r : result) {
			// workaround to avoid unintended chars (ex: FNAC)
			r.setName(StringUtils.toOneLine(r.getName()));

			results.add(new LookupInfo(r.getSymbol(), r.getName(), r.getExchDisp(), r.getTypeDisp(), ProviderCode.YAHOO.name()));
		}

		return results;
	}

	@Override
	public LookupDetails getDetails(String code) throws IOException {
		List<LookupInfo> result = search(code);

		String name = null;

		for (LookupInfo cl : result) {
			if (cl.getCode().equals(code)) {
				name = cl.getName();
				break;
			}
		}

		if (name == null) {
			throw new IllegalArgumentException("Code doesn't have name: " + code);
		}

		String source = parseSource(code);
		String currency = parseTarget(code);

		if (currency == null) {
			String response = handler.sendGet(format(urlInfo, code));

			YahooInfoModel model = mapper.readValue(response, YahooInfoModel.class);

			YahooResult o = model.getResults();

			currency = o.getQuote().getCurrency().toUpperCase();
		}
			
		return new LookupDetails(code, name, source, currency, getProviderCode());
	}

	protected static String parseSource(String code) {
		Matcher stockMatcher = STOCK_PATTERN.matcher(code);

		if (stockMatcher.matches())
			return stockMatcher.group(1);

		Matcher forexMatcher = Yahoo.FOREX_PATTERN.matcher(code);

		if (forexMatcher.matches()) {
			String source = forexMatcher.group(1);
			String target = forexMatcher.group(2);
			return (target != null) ? source : CurrencyUtils.USD;
		}

		return null;
	}

	protected static String parseTarget(String code) {
		Matcher forexMatcher = Yahoo.FOREX_PATTERN.matcher(code);

		if (forexMatcher.matches()) {
			String source = forexMatcher.group(1);
			String target = forexMatcher.group(2);
			return (target != null) ? target : source;
		}

		return null;
	}

	@Override
	public String getProviderCode() {
		return ProviderCode.YAHOO.name();
	}
}
