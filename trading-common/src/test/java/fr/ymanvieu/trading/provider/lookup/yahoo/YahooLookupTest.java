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

import static fr.ymanvieu.trading.TestUtils.readFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.ProviderException;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(MockitoJUnitRunner.class)
public class YahooLookupTest {

	private static String DATA, DATA_INFO, DATA_INFO_NO_CURRENCY;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Mock
	private SymbolRepository symbolRepo;

	private YahooLookup yahooLookup = new YahooLookup();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahooLookup, "rt");

		server = MockRestServiceServer.bindTo(rt).build();

		ReflectionTestUtils.setField(yahooLookup, "url", "");
		ReflectionTestUtils.setField(yahooLookup, "urlInfo", "");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DATA = readFile("/provider/yahoo_lookup/search_ren.json");
		DATA_INFO = readFile("/provider/yahoo_lookup/info.csv");
		DATA_INFO_NO_CURRENCY = readFile("/provider/yahoo_lookup/info_no_currency.csv");
	}

	@Test
	public void testSearch() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA, MediaType.APPLICATION_JSON));

		List<LookupInfo> result = yahooLookup.search(null);

		assertThat(result).extracting("code", "name").containsOnlyOnce(tuple("RNO.PA", "Renault SA"));
	}

	@Test
	public void testGetDetails() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA, MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(DATA_INFO, MediaType.APPLICATION_JSON));

		assertThat(yahooLookup.getDetails("RNO.PA").getCurrency()).isEqualTo("EUR");
	}

	@Test
	public void testGetDetails_NoCurrency() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA, MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(DATA_INFO_NO_CURRENCY, MediaType.APPLICATION_JSON));

		exception.expect(ProviderException.class);
		exception.expectMessage("currency_not_found");

		yahooLookup.getDetails("RNO.PA");
	}

	@Test
	public void testParseSource_Stock() {
		String result = YahooLookup.parseSource("BTCUSD=X");
		assertThat(result).isEqualTo("BTC");
	}

	@Test
	public void testParseSource_StockDefault() {
		String result = YahooLookup.parseSource("XAU=X");
		assertThat(result).isEqualTo("USD");
	}

	@Test
	public void testParseSource_ForexParis() {
		String result = YahooLookup.parseSource("EDF.PA");
		assertThat(result).isEqualTo("EDF");
	}

	@Test
	public void testParseSource_Forex() {
		String result = YahooLookup.parseSource("MSFT");
		assertThat(result).isEqualTo("MSFT");
	}

	@Test
	public void testParseSource_ForexUnderscore() {
		String result = YahooLookup.parseSource("TS_B.TO");
		assertThat(result).isEqualTo("TS_B");
	}

	@Test
	public void testParseTarget_Stock() {
		String result = YahooLookup.parseTarget("BTCUSD=X");
		assertThat(result).isEqualTo("USD");
	}

	@Test
	public void testParseTarget_StockDefault() {
		String result = YahooLookup.parseTarget("XAU=X");
		assertThat(result).isEqualTo("XAU");
	}

	@Test
	public void testParseTarget_NoMatch() {
		String result = YahooLookup.parseTarget("UBI.PA");
		assertThat(result).isNull();
	}
}
