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
package fr.ymanvieu.trading.rate.controller;

import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.DATE_TIME_WITH_TZ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.controller.RateController;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
public class RateControllerTest {

	@Autowired
	private RateController controller;

	@Mock
	private HistoricalRateRepository repoMock;
	
	@Mock
	private LatestRateRepository latestrepo;

	@Mock
	private RateService rateService;

	@InjectMocks
	private RateController mockedController;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@DirtiesContext
	@Sql("/sql/insert_data.sql")
	@Test
	public void testFindLatestByCriteria() throws Exception {
		// when
		Page<LatestRate> result = controller.findLatestByCriteria(USD, EUR, null, null, null, null);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getNumberOfElements()).isEqualTo(1);

		RateEntity r = result.getContent().get(0);

		assertThat(r.getFromcur().getCode()).isEqualTo(USD);
		assertThat(r.getTocur().getCode()).isEqualTo(EUR);
		assertThat(r.getDate()).hasSameTimeAs(DATE_TIME_WITH_TZ.parse("2015-01-30 13:55:00.0 CET"));
		assertThat(r.getValue()).isEqualByComparingTo("0.88");
	}

	@Test
	public void testFindRawValues_NoRange() {
		// when
		mockedController.findRawValues(USD, EUR, null, null);

		// then
		verify(repoMock, times(1)).findWeeklyValues(USD, EUR, null, null);
		verifyNoMoreInteractions(repoMock);
	}

	@Test
	public void testFindRawValues_TwoYearsRange() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2013-08-01 00:05:00.0 CEST");
		Date end = DATE_TIME_WITH_TZ.parse("2015-08-01 00:05:00.0 CEST");

		// when
		mockedController.findRawValues(USD, EUR, start, end);

		// then
		verify(repoMock, times(1)).findWeeklyValues(USD, EUR, start, end);
		verifyNoMoreInteractions(repoMock);
	}

	@Test
	public void testFindRawValues_SixMonthsRange() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2015-02-01 00:05:00.0 CET");
		Date end = DATE_TIME_WITH_TZ.parse("2015-08-01 00:05:00.0 CEST");

		// when
		mockedController.findRawValues(USD, EUR, start, end);

		// then
		verify(repoMock, times(1)).findDailyValues(USD, EUR, start, end);
		verifyNoMoreInteractions(repoMock);
	}

	@Test
	public void testFindRawValues_BelowSixMonthsRange() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2015-02-01 00:05:00.0 CET");
		Date end = DATE_TIME_WITH_TZ.parse("2015-08-01 00:03:00.0 CEST");

		// when
		mockedController.findRawValues(BRE, USD, start, end);

		// then
		verify(repoMock, times(1)).findHourlyValues(BRE, USD, start, end);
		verifyNoMoreInteractions(repoMock);
	}

	@Test
	public void testFindRawValues_JustOverAWeekRange() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2015-02-10 00:05:00.0 CET");
		Date end = DATE_TIME_WITH_TZ.parse("2015-02-17 00:05:00.0 CET");

		// when
		mockedController.findRawValues(BRE, USD, start, end);

		// then
		verify(repoMock, times(1)).findHourlyValues(BRE, USD, start, end);
		verifyNoMoreInteractions(repoMock);
	}

	@Test
	public void testFindRawValues_FewDaysRange() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2015-02-10 00:05:00.0 CET");
		Date end = DATE_TIME_WITH_TZ.parse("2015-02-17 00:04:00.0 CET");

		// when
		mockedController.findRawValues(BRE, USD, start, end);

		// then
		verify(repoMock, times(1)).findDateValues(BRE, USD, start, end);
		verifyNoMoreInteractions(repoMock);
	}
}