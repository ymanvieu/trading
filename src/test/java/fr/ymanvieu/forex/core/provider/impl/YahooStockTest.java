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
package fr.ymanvieu.forex.core.provider.impl;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
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

import fr.ymanvieu.forex.core.Utils;
import fr.ymanvieu.forex.core.http.ConnectionHandler;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;

@RunWith(MockitoJUnitRunner.class)
public class YahooStockTest {

	private static String DATA_INFO, DATA_HISTORY;

	@InjectMocks
	private YahooStock spied;

	@Mock
	private ConnectionHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DATA_INFO = Utils.readFile("/provider/yahoo_stock/info.json");
		DATA_HISTORY = Utils.readFile("/provider/yahoo_stock/history.csv");
	}

	@Test
	public void testGetCurrencyOK() throws Exception {
		doReturn(DATA_INFO).when(handler).sendGet(anyString());

		assertThat(spied.getCurrency("RNO.PA")).isEqualTo("EUR");
	}

	@Test
	public void testGetHistoricalRatesOK() throws Exception {
		RateEntity expectedRate0 = new RateEntity("TOTO", EUR, new BigDecimal("72.88"), DATE_TIME_WITH_TZ.parse("2016-02-04 0:0:0.0 CET"));
		RateEntity expectedRate1 = new RateEntity("TOTO", EUR, new BigDecimal("48.80"), DATE_TIME_WITH_TZ.parse("2000-01-03 0:0:0.0 CET"));

		doReturn(DATA_HISTORY).when(handler).sendGet(anyString());

		assertThat(spied.getHistoricalRates("TOTO", EUR)).hasSize(2).containsOnlyOnce(expectedRate0, expectedRate1);
	}
}
