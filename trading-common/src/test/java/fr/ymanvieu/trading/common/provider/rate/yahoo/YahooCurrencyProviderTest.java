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
package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.provider.Quote;

@ExtendWith({SpringExtension.class})
public class YahooCurrencyProviderTest {

	@Value("classpath:provider/rate/yahoo/latest_currencies.json")
	Resource lastest;

	private MockRestServiceServer server;

	private final YahooCurrencyProvider yahooCurrencyProvider = new YahooCurrencyProvider();

	@BeforeEach
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahooCurrencyProvider, "rt");

		server = MockRestServiceServer.bindTo(rt).build();

		ReflectionTestUtils.setField(yahooCurrencyProvider, "url", "");
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(lastest, MediaType.APPLICATION_JSON));

		Quote expectedUsdEurRate = new Quote(USD, EUR, new BigDecimal("0.8322"), parse("2017-09-11T10:01:00+02:00"));

		List<Quote> result = yahooCurrencyProvider.getRates();

		assertThat(result).containsOnlyOnce(expectedUsdEurRate);
	}
}
