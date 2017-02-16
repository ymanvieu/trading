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
package fr.ymanvieu.trading.provider.rate.quandl;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.TestUtils.readFile;
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

public class QuandlTest {

	private static String BRENT_5DAYS, LATEST_DATA;

	private Quandl quandl = new Quandl();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(quandl, "rt");

		server = MockRestServiceServer.bindTo(rt).build();

		ReflectionTestUtils.setField(quandl, "latestUrl", "");
		ReflectionTestUtils.setField(quandl, "historyUrl", "");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BRENT_5DAYS = readFile("/provider/quandl/brent_20151222_5days.json");
		LATEST_DATA = readFile("/provider/quandl/brent_20160213.json");
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(LATEST_DATA, MediaType.APPLICATION_JSON));

		assertThat(quandl.getRates()) //
				.containsExactly(quote("BRE", USD, new BigDecimal("31.31"), parse("2016-02-13 00:00:00.0 GMT")));
	}

	@Test
	public void testGetHistoricalRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(BRENT_5DAYS, MediaType.APPLICATION_JSON));

		assertThat(quandl.getHistoricalRates()).hasSize(5);
	}
}