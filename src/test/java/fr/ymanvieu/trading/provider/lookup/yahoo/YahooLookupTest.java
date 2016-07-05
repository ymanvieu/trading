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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.ymanvieu.trading.TestUtils;
import fr.ymanvieu.trading.http.ConnectionHandler;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(MockitoJUnitRunner.class)
public class YahooLookupTest {

	private static String DATA, DATA_INFO;

	@InjectMocks
	private YahooLookup spied;

	@Mock
	private ConnectionHandler handler;

	@Mock
	private SymbolRepository symbolRepo;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DATA = TestUtils.readFile("/provider/yahoo_lookup/search_ren.json");
		DATA_INFO = TestUtils.readFile("/provider/yahoo_lookup/info.json");
	}

	@Test
	public void testSearch() throws Exception {
		when(handler.sendGet(anyString())).thenReturn(DATA);

		List<LookupInfo> result = spied.search(null);

		assertThat(result).extracting("code", "name").containsOnlyOnce( //
				tuple("RNO.PA", "Renault SA"));
	}

	@Test
	public void testGetDetails() throws Exception {
		when(handler.sendGet(anyString())).thenReturn(DATA, DATA_INFO);

		assertThat(spied.getDetails("RNO.PA").getCurrency()).isEqualTo("EUR");
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
