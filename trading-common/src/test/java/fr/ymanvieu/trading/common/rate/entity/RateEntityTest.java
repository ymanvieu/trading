package fr.ymanvieu.trading.common.rate.entity;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;


public class RateEntityTest {

	private static final Instant DEFAULT_TEST_DATE = parse("2014-12-18T14:00:00Z");

	@Test
	public void testEqualsOK() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;
		BigDecimal v1 = new BigDecimal("1.5465");
		BigDecimal v2 = new BigDecimal("1.5465");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);
	}

	@Test
	public void testEqualsOK_SameObject() {
		Instant d1 = DEFAULT_TEST_DATE;
		BigDecimal v1 = BigDecimal.valueOf(1.5f);

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);

		assertThat(r1).isEqualTo(r1);
	}
	
	@Test
	public void testEquals_SameValuesNotSameFraction() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.500000");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);
	}
	
	@Test
	public void testEquals_Subclasses() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		HistoricalRate r1 = new HistoricalRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);		
	}

	@Test
	public void testEqualsKO_DifferentDates() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = parse("2014-12-19T14:00:01Z");

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesFrom() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(EUR, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesTo() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, EUR, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentValue() {
		Instant d1 = DEFAULT_TEST_DATE;
		Instant d2 = DEFAULT_TEST_DATE;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.6");

		LatestRate r1 = new LatestRate(GBP, USD, v1, d1);
		LatestRate r2 = new LatestRate(GBP, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}
}
