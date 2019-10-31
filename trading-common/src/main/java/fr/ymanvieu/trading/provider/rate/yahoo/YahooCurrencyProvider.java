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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.rate.Rate;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

@Component
public class YahooCurrencyProvider implements LatestRateProvider {

	private static final Pattern FOREX_PATTERN = Pattern.compile("(\\w{3})(\\w{3})?=X");

	@Value("${provider.yahoo.url.latest.currencies}")
	private String url;

	private final RestTemplate rt = new RestTemplate();

	@Override
	public List<Rate> getRates() throws IOException {
		YahooModel result = rt.getForObject(url, YahooModel.class);

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
					
					return new Rate(source, target, BigDecimal.valueOf(yf.getRegularMarketPrice()), inst);
				})
				.collect(Collectors.toList());
	}
}