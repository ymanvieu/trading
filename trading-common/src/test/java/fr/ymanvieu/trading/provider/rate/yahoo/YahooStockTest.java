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
import static fr.ymanvieu.trading.TestUtils.readFile;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.rate.Quote;

@RunWith(MockitoJUnitRunner.class)
public class YahooStockTest {

	private static String DATA_HISTORY, DATA_LATEST;

	@Mock
	private PairService pairService;

	@InjectMocks
	private YahooStock yahooStock = new YahooStock();

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahooStock, "rt");
		
		server = MockRestServiceServer.bindTo(rt).build();
		
		ReflectionTestUtils.setField(yahooStock, "url", "");
		ReflectionTestUtils.setField(yahooStock, "urlHistory", "");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DATA_HISTORY = readFile("/provider/yahoo_stock/history.csv");
		DATA_LATEST = readFile("/provider/yahoo_stock/latest.json");
	}

	@Test
	public void testGetHistoricalRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA_HISTORY, MediaType.TEXT_PLAIN));
		
		Quote expectedRate0 = quote("TOTO", null, new BigDecimal("72.88"), parse("2016-02-04 00:00:00.0 CET"));
		Quote expectedRate1 = quote("TOTO", null, new BigDecimal("48.80"), parse("2000-01-03 00:00:00.0 CET"));

		assertThat(yahooStock.getHistoricalRates("TOTO")).containsExactlyInAnyOrder(expectedRate0, expectedRate1);
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA_LATEST, MediaType.APPLICATION_JSON));

		List<PairEntity> symbols = asList(new PairEntity("UBI.PA", "Ubi", "UBI", EUR, "YAHOO"));
		when(pairService.getAll()).thenReturn(symbols);

		List<Quote> result = yahooStock.getRates();

		assertThat(result).containsExactly(quote("UBI", EUR, new BigDecimal("26.35"), parse("2016-03-18 17:35:16.0 CET")));
	}

	@Test
	public void testGetLatestRate() throws Exception {
		server.expect(anything()).andRespond(withSuccess(DATA_LATEST, MediaType.APPLICATION_JSON));

		Quote result = yahooStock.getLatestRate(null);

		assertThat(result).isEqualTo(quote("UBI.PA", null, new BigDecimal("26.35"), parse("2016-03-18 17:35:16.0 CET")));
	}
}
