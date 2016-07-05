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
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.CHF;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.math.BigDecimal;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fr.ymanvieu.trading.TestUtils;
import fr.ymanvieu.trading.http.ConnectionHandler;
import fr.ymanvieu.trading.provider.rate.ecb.EuropeanCentralBank;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.util.DateUtils;

@RunWith(MockitoJUnitRunner.class)
public class EuropeanCentralBankTest {

	@InjectMocks
	private EuropeanCentralBank spied;

	@Mock
	private ConnectionHandler handler;

	private static String MOCK_HIST;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MOCK_HIST = TestUtils.readFile("/provider/ecb/eurofxref-hist-90d.xml");
	}

	@Test
	public void testGetHistoricalRatesOK() throws Exception {
		doReturn(MOCK_HIST).when(handler).sendGet(anyString());

		Quote rateEurUsd = quote(USD, EUR, new BigDecimal("0.8607333448"), DateUtils.parse("2015-01-22 15:00:00.0 CET"));
		Quote rateEurGbp = quote(USD, GBP, new BigDecimal("0.66381076549656"), DateUtils.parse("2015-03-10 15:00:00.0 CET"));
		Quote rateEurChf = quote(USD, CHF, new BigDecimal("0.95813084117036"), DateUtils.parse("2015-04-21 15:00:00.0 CEST"));
		List<Quote> r = spied.getHistoricalRates();

		assertThat(r).hasSize(1922);
		assertThat(r).containsOnlyOnce(rateEurUsd, rateEurChf, rateEurGbp);
	}
}
