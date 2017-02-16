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
package fr.ymanvieu.trading.rate.repository;

import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.rate.DateValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_data_histo.sql")
public class HistoricalRateRepositoryTest {

	private static final double OFFSET = 0.0001;

	@Autowired
	private HistoricalRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Test
	public void testFindDateValues() throws Exception {
		// given
		Date start = parse("2015-02-01 00:15:00.0 CET");
		Date end = parse("2015-02-02 15:59:00.0 CET");

		// when
		List<DateValue> result = repo.findDateValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).hasSameTimeAs(parse("2015-02-01 22:42:10.0 CET"));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.883353);

		DateValue rate1 = result.get(1);
		assertThat(rate1.getDate()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
		assertThat(rate1.getValue()).isEqualByComparingTo(0.882044);
	}

	@Test
	public void testFindDailyValues_OneDay() throws Exception {
		// given
		Date start = parse("2015-02-01 00:00:00.0 CET");
		Date end = parse("2015-02-01 23:59:59.0 CET");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).hasSameTimeAs(parse("2015-02-01 00:15:00.0 CET"));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.851);
	}

	@Test
	public void testFindDailyValues_OneValueInRange() throws Exception {
		// given
		Date start = parse("2015-02-01 00:00:00.0 CET");
		Date end = parse("2015-02-01 02:31:00.0 CET");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).hasSameTimeAs(parse("2015-02-01 00:15:00.0 CET"));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.802);
	}

	@Test
	public void testFindDailyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-02-10 00:05:00.0 CET");
		Date end = parse("2015-02-10 22:31:00.0 CET");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindDailyValues_TwoDays() throws Exception {
		// given
		Date start = parse("2015-02-10 00:00:00.0 CET");
		Date end = parse("2015-02-12 00:00:00.0 CET");

		// when
		List<DateValue> result = repo.findDailyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat(rate.getValue()).isCloseTo(51.97, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-11 00:05:00.0 CET"));
		assertThat(rate.getValue()).isCloseTo(51.4, within(OFFSET));
	}

	@Test
	public void testFindHourlyValues_OneDay() throws Exception {
		// given
		Date start = parse("2015-02-12 00:00:00.0 CET");
		Date end = parse("2015-02-12 23:59:59.0 CET");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-12 00:47:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.6);

		rate = result.get(1);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-12 22:05:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.5);

		rate = result.get(2);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-12 23:59:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(50.6);
	}

	@Test
	public void testFindHourlyValues_TwoDays() throws Exception {
		// given
		Date start = parse("2015-02-10 00:15:00.0 CET");
		Date end = parse("2015-02-11 01:59:00.0 CET");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(55.24);

		rate = result.get(1);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-10 12:00:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.7);

		rate = result.get(2);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-11 00:05:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(50.1);
	}

	@Test
	public void testFindHourlyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-03-10 00:15:00.0 CET");
		Date end = parse("2015-03-11 01:59:00.0 CET");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindWeeklyValues_TwoWeeksInTwoYearsRange() throws Exception {
		// given
		Date start = parse("2015-02-10 00:14:00.0 CET");
		Date end = parse("2016-03-28 00:00:00.0 CET");

		// when
		List<DateValue> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat(rate.getValue()).isCloseTo(50.17111111111111, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2016-03-21 00:03:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(52.5);
	}
	
	@Test
	public void testFindValues_TwoWeeksInTwoYearsRange() throws Exception {
		// given
		Date start = parse("2014-03-10 00:14:00.0 CET");
		Date end = parse("2016-03-28 00:00:00.0 CET");

		// when
		List<DateValue> result = repo.findValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat(rate.getValue()).isCloseTo(50.17111111111111, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).hasSameTimeAs(parse("2016-03-21 00:03:00.0 CET"));
		assertThat(rate.getValue()).isEqualByComparingTo(52.5);
	}

	@Test
	public void testFindWeeklyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-03-10 00:15:00.0 CET");
		Date end = parse("2015-03-11 01:59:00.0 CET");

		// when
		List<DateValue> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}
}
