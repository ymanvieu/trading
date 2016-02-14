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
package fr.ymanvieu.forex.core.web;

import static fr.ymanvieu.forex.core.provider.impl.Quandl.BRE;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
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

import fr.ymanvieu.forex.core.ForexApplication;
import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
public class RateControllerTest {

	@Autowired
	private RateController controller;

	@Mock
	private HistoricalRateRepository repoMock;

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

		assertThat(r.getFromcur()).isEqualTo(USD);
		assertThat(r.getTocur()).isEqualTo(EUR);
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