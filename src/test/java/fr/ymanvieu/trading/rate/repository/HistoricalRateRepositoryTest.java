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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HistoricalRateRepositoryTest {

	public static final double OFFSET = 0.0001;

	@Autowired
	private HistoricalRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Test
	public void testFindDateValues() throws Exception {
		// given
		Date start = parse("2015-02-01 00:15:00.0 CET");
		Date end = parse("2015-02-02 15:59:00.0 CET");

		// when
		List<Object[]> result = repo.findDateValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(2);

		Object[] rate0 = result.get(0);
		assertThat((Date) rate0[0]).hasSameTimeAs(parse("2015-02-01 22:42:10.0 CET"));
		assertThat((BigDecimal) rate0[1]).isEqualByComparingTo("0.883353");

		Object[] rate1 = result.get(1);
		assertThat((Date) rate1[0]).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
		assertThat((BigDecimal) rate1[1]).isEqualByComparingTo("0.882044");
	}

	@Sql({ "/sql/insert_data.sql", "/sql/insert_eur_gbp.sql" })
	@Test
	@Transactional
	public void testDeleteByFromcurCodeAndTocurCode() {
		// when
		int result = repo.deleteByFromcurCodeOrTocurCode(EUR);

		// then
		assertThat(result).isEqualTo(3);
		assertThat(repo.count()).isEqualTo(9);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindDailyValues_OneDay() throws Exception {
		// given
		Date start = parse("2015-02-01 00:00:00.0 CET");
		Date end = parse("2015-02-01 23:59:59.0 CET");

		// when
		List<Object[]> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		Object[] rate0 = result.get(0);
		assertThat((Date) rate0[0]).hasSameTimeAs(parse("2015-02-01 00:15:00.0 CET"));
		assertThat((Double) rate0[1]).isEqualByComparingTo(0.851);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindDailyValues_OneValueInRange() throws Exception {
		// given
		Date start = parse("2015-02-01 00:00:00.0 CET");
		Date end = parse("2015-02-01 02:31:00.0 CET");

		// when
		List<Object[]> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		Object[] rate0 = result.get(0);
		assertThat((Date) rate0[0]).hasSameTimeAs(parse("2015-02-01 00:15:00.0 CET"));
		assertThat((Double) rate0[1]).isEqualByComparingTo(0.802);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindDailyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-02-10 00:05:00.0 CET");
		Date end = parse("2015-02-10 22:31:00.0 CET");

		// when
		List<Object[]> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindDailyValues_TwoDays() throws Exception {
		// given
		Date start = parse("2015-02-10 00:00:00.0 CET");
		Date end = parse("2015-02-12 00:00:00.0 CET");

		// when
		List<Object[]> result = repo.findDailyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		Object[] rate = result.get(0);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat((Double) rate[1]).isEqualTo(51.97, within(OFFSET));

		rate = result.get(1);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-11 00:05:00.0 CET"));
		assertThat((Double) rate[1]).isEqualTo(51.4, within(OFFSET));
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindHourlyValues_OneDay() throws Exception {
		// given
		Date start = parse("2015-02-12 00:00:00.0 CET");
		Date end = parse("2015-02-12 23:59:59.0 CET");

		// when
		List<Object[]> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		Object[] rate = result.get(0);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-12 00:47:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(48.6);

		rate = result.get(1);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-12 22:05:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(48.5);

		rate = result.get(2);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-12 23:59:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(50.6);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindHourlyValues_TwoDays() throws Exception {
		// given
		Date start = parse("2015-02-10 00:15:00.0 CET");
		Date end = parse("2015-02-11 01:59:00.0 CET");

		// when
		List<Object[]> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		Object[] rate = result.get(0);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(55.24);

		rate = result.get(1);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-10 12:00:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(48.7);

		rate = result.get(2);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-11 00:05:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(50.1);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindHourlyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-03-10 00:15:00.0 CET");
		Date end = parse("2015-03-11 01:59:00.0 CET");

		// when
		List<Object[]> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindWeeklyValues_TwoWeeksInTwoYearsRange() throws Exception {
		// given
		Date start = parse("2015-02-10 00:14:00.0 CET");
		Date end = parse("2016-03-28 00:00:00.0 CET");

		// when
		List<Object[]> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		Object[] rate = result.get(0);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2015-02-10 00:15:00.0 CET"));
		assertThat((Double) rate[1]).isEqualTo(50.17111111111111, within(OFFSET));

		rate = result.get(1);
		assertThat((Date) rate[0]).hasSameTimeAs(parse("2016-03-21 00:03:00.0 CET"));
		assertThat((Double) rate[1]).isEqualByComparingTo(52.5);
	}

	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testFindWeeklyValues_NoDataForRange() throws Exception {
		// given
		Date start = parse("2015-03-10 00:15:00.0 CET");
		Date end = parse("2015-03-11 01:59:00.0 CET");

		// when
		List<Object[]> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}
}
