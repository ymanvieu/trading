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
package fr.ymanvieu.trading.provider.rate.ecb;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.TestUtils.readFile;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.CHF;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.rate.Quote;

public class EuropeanCentralBankTest {

	private static String MOCK_HIST;

	private EuropeanCentralBank ecb = new EuropeanCentralBank();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(ecb, "rt");

		server = MockRestServiceServer.bindTo(rt).build();

		ReflectionTestUtils.setField(ecb, "urlHistory", "");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MOCK_HIST = readFile("/provider/ecb/eurofxref-hist.xml");
	}

	@Test
	public void testGetHistoricalRatesOK() throws Exception {

		server.expect(anything()).andRespond(withSuccess(MOCK_HIST, MediaType.APPLICATION_XML));

		Quote rateEurUsd = quote(USD, EUR, new BigDecimal("0.8607333448"), parse("2015-01-22T14:15:00+01:00"));
		Quote rateEurGbp = quote(USD, GBP, new BigDecimal("0.66381076549656"), parse("2015-03-10T14:15:00+01:00"));
		Quote rateEurChf = quote(USD, CHF, new BigDecimal("0.95813084117036"), parse("2015-04-21T14:15:00+02:00"));

		List<Quote> r = ecb.getHistoricalRates();

		assertThat(r).hasSize(248);
		assertThat(r).containsOnlyOnce(rateEurUsd, rateEurChf, rateEurGbp);
	}
}
