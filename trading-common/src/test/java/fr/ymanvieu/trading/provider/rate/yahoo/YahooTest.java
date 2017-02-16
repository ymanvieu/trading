/**
 * Copyright (C) 2015 Yoann Manvieu
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

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.TestUtils.readFile;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.rate.Quote;

public class YahooTest {

	private static String MOCK_ALL;

	private Yahoo yahoo = new Yahoo();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahoo, "rt");
		
		server = MockRestServiceServer.bindTo(rt).build();
		
		ReflectionTestUtils.setField(yahoo, "urlCurrencies", "");
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MOCK_ALL = readFile("/provider/yahoo/quote_20141219.json");
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(MOCK_ALL, MediaType.APPLICATION_JSON));
		
		Quote expectedRate = quote(USD, EUR, new BigDecimal("0.817595"), parse("2014-12-19 22:40:32.0 CET"));

		assertThat(yahoo.getRates()).hasSize(172).containsOnlyOnce(expectedRate);
	}
}