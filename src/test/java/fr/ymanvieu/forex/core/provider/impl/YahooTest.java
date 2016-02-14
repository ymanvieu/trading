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
package fr.ymanvieu.forex.core.provider.impl;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
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
public class YahooTest {

	private static String MOCK_ALL;

	@InjectMocks
	private Yahoo spied;

	@Mock
	private ConnectionHandler handler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MOCK_ALL = Utils.readFile("/provider/yahoo/quote_20141219.json");
	}

	@Test
	public void testGetRatesOK() throws Exception {
		RateEntity expectedRate = new RateEntity(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 22:40:32.0 CET"));

		doReturn(MOCK_ALL).when(handler).sendGet(anyString());

		assertThat(spied.getRates()).hasSize(172).containsOnlyOnce(expectedRate);
	}
}
