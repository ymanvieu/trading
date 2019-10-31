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

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.io.ClasspathFileReader.readFile;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.rate.Rate;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@RunWith(MockitoJUnitRunner.class)
public class YahooStockProviderTest {

	private static final String HISTO_AAPL = readFile("/provider/rate/yahoo/histo_aapl.json"); 
	private static final String LATEST = readFile("/provider/rate/yahoo/latest.json");
	private static final String LATEST_UBI = readFile("/provider/rate/yahoo/latest_ubi.json");
	
	@Mock
	private PairService pairService;

	@InjectMocks
	private YahooStockProvider yahooStock;

	private MockRestServiceServer server;

	@Before
	public void setUpBefore() {
		RestTemplate rt = (RestTemplate) ReflectionTestUtils.getField(yahooStock, "rt");
		
		server = MockRestServiceServer.bindTo(rt).build();
		
		ReflectionTestUtils.setField(yahooStock, "url", "");
		ReflectionTestUtils.setField(yahooStock, "urlHistory", "");
	}

	@Test
	public void testGetHistoricalRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(HISTO_AAPL, MediaType.APPLICATION_JSON));
		
		Rate expectedRate0 = new Rate("AAPL", USD, new BigDecimal("0.5133928656578064"), parse("1980-12-12T09:00:00+00:00"));
		Rate expectedRate3 = new Rate("AAPL", USD, new BigDecimal("160.86000061035156"), parse("2017-09-08T22:00:00+02:00"));

		List<Rate> result = yahooStock.getHistoricalRates("AAPL");
		
		assertThat(result).hasSize(443).containsOnlyOnce(expectedRate0, expectedRate3);
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(LATEST, MediaType.APPLICATION_JSON));
		
		Rate expectedUbisoftRate = new Rate("UBI", EUR, new BigDecimal("57.52"), parse("2017-09-08T17:35:14+02:00"));
		Rate expectedLitecoinRate = new Rate("LTC", USD, new BigDecimal("67.29928"), parse("2017-09-10T15:19:23+02:00"));
		

		List<PairEntity> symbols = Arrays.asList(new PairEntity("UBI.PA", "Ubi", new SymbolEntity("UBI"), new SymbolEntity(EUR), null, "YAHOO"),
				new PairEntity("LTCUSD=X", "Litecoin", new SymbolEntity("LTC"), new SymbolEntity(USD), null, "YAHOO"));
		
		when(pairService.getAllFromProvider(ArgumentMatchers.anyString())).thenReturn(symbols);

		List<Rate> result = yahooStock.getRates();

		assertThat(result).containsOnlyOnce(expectedLitecoinRate, expectedUbisoftRate);
	}

	@Test
	public void testGetLatestRate() {
		server.expect(anything()).andRespond(withSuccess(LATEST_UBI, MediaType.APPLICATION_JSON));

		Rate result = yahooStock.getLatestRate("UBI.PA");

		assertThat(result).isEqualTo(new Rate("UBI.PA", null, new BigDecimal("57.52"), parse("2017-09-08T17:35:14+02:00")));
	}
}
