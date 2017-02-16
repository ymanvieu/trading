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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooModel.YahooFields;
import fr.ymanvieu.trading.provider.rate.yahoo.YahooModel.YahooResource;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

/**
 * Supporting 172 currencies including : <br>
 * <ul>
 * <li>Copper (XCP)</li>
 * <li>Zambian kwacha (ZMW)</li>
 * </ul>
 */
@Component
public class Yahoo implements LatestRateProvider {

	// public for YahooLookup
	public static final Pattern FOREX_PATTERN = Pattern.compile("(\\w{3})(\\w{3})?=X");

	@Value("${provider.yahoo.url.latest.currencies}")
	private String urlCurrencies;

	protected final RestTemplate rt = new RestTemplate();

	protected YahooModel sendGet(String url) {
		return rt.exchange(url, HttpMethod.GET, requestEntity(), YahooModel.class).getBody();
	}

	// workaround (http response code 406 when non-mobile user-agent)
	protected HttpEntity<String> requestEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("User-Agent", "Android");

		return new HttpEntity<>(headers);
	}

	@Override
	public List<Quote> getRates() throws IOException {
		YahooModel model = sendGet(urlCurrencies);

		List<Quote> rates = new ArrayList<>();

		for (YahooResource o : model.getList().getResources()) {
			YahooFields yf = o.getResource().getFields();

			Matcher m = FOREX_PATTERN.matcher(yf.getSymbol());

			m.matches();
			String source = m.group(1);
			String target = m.group(2);

			if (target == null) {
				target = source;
				source = CurrencyUtils.USD;
			}

			if (source.equals(target)) {
				continue;
			}

			rates.add(new Quote(source, target, yf.getPrice(), yf.getUtctime()));
		}

		return rates;
	}
}