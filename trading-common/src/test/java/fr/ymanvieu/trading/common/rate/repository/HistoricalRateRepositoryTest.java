package fr.ymanvieu.trading.common.rate.repository;

import static fr.ymanvieu.trading.common.symbol.Currency.EUR;
import static fr.ymanvieu.trading.common.symbol.Currency.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.rate.DateValue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("/sql/insert_data_histo.sql")
public class HistoricalRateRepositoryTest {

	private static final double OFFSET = 0.0001;
	
	private static final String BRE = "BRE";

	@Autowired
	private HistoricalRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Test
	public void testFindDateValues() {
		// given
		Instant start = parse("2015-02-01T00:15:00");
		Instant end = parse("2015-02-02T15:59:00");

		// when
		List<DateValue> result = repo.findDateValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).isEqualTo((parse("2015-02-01T22:42:10")));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.883353);

		DateValue rate1 = result.get(1);
		assertThat(rate1.getDate()).isEqualTo(parse("2015-02-02T08:42:50"));
		assertThat(rate1.getValue()).isEqualByComparingTo(0.882044);
	}

	@Test
	public void testFindDailyValues_OneDay() {
		// given
		Instant start = parse("2015-02-01T00:00:00");
		Instant end = parse("2015-02-01T23:59:59");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).isEqualTo(parse("2015-02-01T00:15:00"));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.851);
	}

	@Test
	public void testFindDailyValues_OneValueInRange() {
		// given
		Instant start = parse("2015-02-01T00:00:00");
		Instant end = parse("2015-02-01T02:31:00");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(1);

		DateValue rate0 = result.get(0);
		assertThat(rate0.getDate()).isEqualTo(parse("2015-02-01T00:15:00"));
		assertThat(rate0.getValue()).isEqualByComparingTo(0.802);
	}

	@Test
	public void testFindDailyValues_NoDataForRange() {
		// given
		Instant start = parse("2015-02-10T00:05:00");
		Instant end = parse("2015-02-10T22:31:00");

		// when
		List<DateValue> result = repo.findDailyValues(USD, EUR, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindDailyValues_TwoDays() {
		// given
		Instant start = parse("2015-02-10T00:00:00");
		Instant end = parse("2015-02-12T00:00:00");

		// when
		List<DateValue> result = repo.findDailyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-10T00:15:00"));
		assertThat(rate.getValue()).isCloseTo(51.97, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-11T00:05:00"));
		assertThat(rate.getValue()).isCloseTo(51.4, within(OFFSET));
	}

	@Test
	public void testFindHourlyValues_OneDay() {
		// given
		Instant start = parse("2015-02-12T00:00:00");
		Instant end = parse("2015-02-12T23:59:59");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-12T00:47:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.6);

		rate = result.get(1);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-12T22:05:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.5);

		rate = result.get(2);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-12T23:59:00.0"));
		assertThat(rate.getValue()).isEqualByComparingTo(50.6);
	}

	@Test
	public void testFindHourlyValues_TwoDays() {
		// given
		Instant start = parse("2015-02-10T00:15:00");
		Instant end = parse("2015-02-11T01:59:00");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(3);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-10T00:15:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(55.24);

		rate = result.get(1);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-10T12:00:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(48.7);

		rate = result.get(2);
		assertThat(rate.getDate()).isEqualTo(parse("2015-02-11T00:05:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(50.1);
	}

	@Test
	public void testFindHourlyValues_NoDataForRange() {
		// given
		Instant start = parse("2015-03-10T00:15:00");
		Instant end = parse("2015-03-11T01:59:00");

		// when
		List<DateValue> result = repo.findHourlyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}

	@Test
	public void testFindWeeklyValues_TwoWeeksInTwoYearsRange() {
		// given
		Instant start = parse("2016-10-04T00:14:00");
		Instant end = parse("2018-11-11T00:00:00");

		// when
		List<DateValue> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).isEqualTo(parse("2016-10-04T00:15:00"));
		assertThat(rate.getValue()).isCloseTo(50.17111111111111, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).isEqualTo(parse("2018-11-06T00:03:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(52.5);
	}
	
	@Test
	public void testFindValues_TwoWeeksInTwoYearsRange() {
		// given
		Instant start = parse("2016-10-04T00:14:00");
		Instant end = parse("2018-11-11T00:00:00");

		// when
		List<DateValue> result = repo.findValues(BRE, USD, start, end);

		// then
		assertThat(result).hasSize(2);

		DateValue rate = result.get(0);
		assertThat(rate.getDate()).isEqualTo(parse("2016-10-04T00:15:00"));
		assertThat(rate.getValue()).isCloseTo(50.17111111111111, within(OFFSET));

		rate = result.get(1);
		assertThat(rate.getDate()).isEqualTo(parse("2018-11-06T00:03:00"));
		assertThat(rate.getValue()).isEqualByComparingTo(52.5);
	}

	@Test
	public void testFindWeeklyValues_NoDataForRange() {
		// given
		Instant start = parse("2015-03-10T00:15:00");
		Instant end = parse("2015-03-11T01:59:00");

		// when
		List<DateValue> result = repo.findWeeklyValues(BRE, USD, start, end);

		// then
		assertThat(result).isEmpty();
	}
}
