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
package fr.ymanvieu.trading.provider.rate.yahoo;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.ymanvieu.trading.TestUtils;
import fr.ymanvieu.trading.http.ConnectionHandler;
import fr.ymanvieu.trading.provider.Pair;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.symbol.Symbol;

@RunWith(MockitoJUnitRunner.class)
public class YahooStockTest {

	private static String DATA_HISTORY, DATA_LATEST;

	@InjectMocks
	private YahooStock spied;

	@Mock
	private ConnectionHandler handler;

	@Mock
	private PairService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DATA_HISTORY = TestUtils.readFile("/provider/yahoo_stock/history.csv");
		DATA_LATEST = TestUtils.readFile("/provider/yahoo_stock/latest.json");
	}

	@Test
	public void testGetHistoricalRatesOK() throws Exception {
		Quote expectedRate0 = quote("TOTO", null, new BigDecimal("72.88"), parse("2016-02-04 0:0:0.0 CET"));
		Quote expectedRate1 = quote("TOTO", null, new BigDecimal("48.80"), parse("2000-01-03 0:0:0.0 CET"));

		doReturn(DATA_HISTORY).when(handler).sendGet(anyString());

		assertThat(spied.getHistoricalRates("TOTO")).hasSize(2).containsOnlyOnce(expectedRate0, expectedRate1);
	}

	@Test
	public void testGetRates() throws Exception {
		doReturn(DATA_LATEST).when(handler).sendGet(anyString());
		List<Pair> symbols = Arrays.asList(new Pair("UBI.PA", "Ubi", new Symbol("UBI", "Ubi", null), new Symbol(EUR, "Euro", "eu"), "YAHOO"));
		when(service.getAll()).thenReturn(symbols);

		List<Quote> result = spied.getRates();

		assertThat(result).hasSize(1);
		assertThat(result).contains(quote("UBI", EUR, new BigDecimal("26.35"), parse("2016-03-18 17:35:16.0 CET")));
	}

	@Test
	public void testGetLatestRate() throws Exception {
		doReturn(DATA_LATEST).when(handler).sendGet(anyString());

		Quote result = spied.getLatestRate(null);

		assertThat(result).isEqualTo(quote("UBI.PA", null, new BigDecimal("26.35"), parse("2016-03-18 17:35:16.0 CET")));
	}

	// FIXME add getName()
}
