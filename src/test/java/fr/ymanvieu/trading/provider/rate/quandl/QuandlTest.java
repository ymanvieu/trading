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
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.ymanvieu.trading.TestUtils;
import fr.ymanvieu.trading.http.ConnectionHandler;

@RunWith(MockitoJUnitRunner.class)
public class QuandlTest {

	@InjectMocks
	private Quandl spied;

	@Mock
	private ConnectionHandler handler;

	private static String BRENT_5DAYS, LATEST_DATA;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BRENT_5DAYS = TestUtils.readFile("/provider/quandl/brent_20151222_5days.json");
		LATEST_DATA = TestUtils.readFile("/provider/quandl/brent_20160213.json");
	}

	@Test
	public void testGetRates() throws Exception {
		doReturn(LATEST_DATA).when(handler).sendGet(anyString());

		assertThat(spied.getRates()).hasSize(1)//
				.containsOnly(quote("BRE", USD, new BigDecimal("31.31"), parse("2016-02-13 00:00:00.0 GMT")));
	}

	@Test
	public void testGetHistoricalRates() throws Exception {
		doReturn(BRENT_5DAYS).when(handler).sendGet(anyString());

		assertThat(spied.getHistoricalRates()).hasSize(5);
	}
}
