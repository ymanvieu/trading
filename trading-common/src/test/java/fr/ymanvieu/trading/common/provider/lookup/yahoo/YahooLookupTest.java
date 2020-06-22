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
package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import static fr.ymanvieu.trading.test.io.ClasspathFileReader.readFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.provider.LookupInfo;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class YahooLookupTest {

	private static final String SEARCH_RESULT = readFile("/provider/lookup/yahoo/search_ubi.json");
	private static final String LATEST_UBI = readFile("/provider/rate/yahoo/latest_ubi.json");
	
	private YahooLookup yahooLookup = new YahooLookup();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahooLookup, "rt");

		server = MockRestServiceServer.bindTo(rt).build();

		ReflectionTestUtils.setField(yahooLookup, "url", "");
		ReflectionTestUtils.setField(yahooLookup, "urlLatest", "");
	}

	@Test
	public void testSearch() throws Exception {
		server.expect(anything()).andRespond(withSuccess(SEARCH_RESULT, MediaType.APPLICATION_JSON));

		List<LookupInfo> result = yahooLookup.search("ubi");

		assertThat(result).containsOnlyOnce(new LookupInfo("UBI.PA", "Ubisoft Entertainment SA", "Paris", "Equity", "YAHOO"));
	}

	@Test
	public void testGetDetails() throws Exception {
		server.expect(anything()).andRespond(withSuccess(SEARCH_RESULT, MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(LATEST_UBI, MediaType.APPLICATION_JSON));

		assertThat(yahooLookup.getDetails("UBI.PA").getCurrency()).isEqualTo("EUR");
	}
	

	protected static Object[][] parametersForTestParseSource() {
		return new Object[][] {
				{ "BTCUSD=X", "BTC" },
				{ "XAU=X", "USD" },
				{ "EDF.PA", "EDF" },
				{ "MSFT", "MSFT" },
				{ "TS_B.TO", "TS_B" },
				{ "CL=F", "CL=F" },
				{ "DOGE-USD", "DOGE" },
				{ "THQN-B.ST", "THQN-B" },
				{ "RDS-A", "RDS-A" },
				{ "005930.KS", "005930" },
		};
	}

	@Test
	@Parameters
	public void testParseSource(String code, String expectedResult) {
		assertThat(YahooLookup.parseSource(code)).isEqualTo(expectedResult);
	}
	
	protected static Object[][] parametersForTestParseTarget() {
		return new Object[][] {
				{ "BTCUSD=X", "USD" },
				{ "XAU=X", "XAU" },
				{ "EDF.PA", null },
				{ "MSFT", null },
				{ "TS_B.TO", null },
				{ "CL=F", null },
				{ "DOGE-USD", "USD" },
				{ "THQN-B.ST", null },
				{ "RDS-A", null },
				{ "005930.KS", null },
		};
	}
	
	@Test
	@Parameters
	public void testParseTarget(String code, String expectedResult) {
		assertThat(YahooLookup.parseTarget(code)).isEqualTo(expectedResult);
	}
}
