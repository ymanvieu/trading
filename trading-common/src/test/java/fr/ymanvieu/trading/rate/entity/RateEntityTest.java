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
package fr.ymanvieu.trading.rate.entity;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class RateEntityTest {

	private static Date date;

	@BeforeClass
	public static void createDates() throws Exception {
		date = parse("2014-12-18 15:00:00.0 CET");
	}

	@Test
	public void testEqualsOK() {
		Date d1 = date;
		Date d2 = date;
		BigDecimal v1 = new BigDecimal("1.5465");
		BigDecimal v2 = new BigDecimal("1.5465");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);
	}

	@Test
	public void testEqualsOK_SameObject() {
		Date d1 = date;
		BigDecimal v1 = BigDecimal.valueOf(1.5f);

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);

		assertThat(r1).isEqualTo(r1);
	}
	
	@Test
	public void testEquals_SameWithTimestampForDates() {
		Date d1 = new Timestamp(date.getTime());
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);
	}
	
	@Test
	public void testEquals_SameValuesNotSameFraction() {
		Date d1 = date;
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.500000");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertThat(r1).isEqualTo(r2);
	}
	
	@Test
	public void testEquals_Subclasses() {
		Date d1 = date;
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new HistoricalRate(new RateEntity(GBP, USD, v1, d1));
		RateEntity r2 = new LatestRate(new RateEntity(GBP, USD, v2, d2));

		assertThat(r1).isEqualTo(r2);		
	}

	@Test
	public void testEqualsKO_DifferentDates() throws Exception {
		Date d1 = date;
		Date d2 = parse("2014-12-19 15:00:00.02 CET");

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesFrom() {
		Date d1 = date;
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(EUR, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesTo() {
		Date d1 = date;
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, EUR, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}

	@Test
	public void testEqualsKO_DifferentValue() {
		Date d1 = date;
		Date d2 = date;

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.6");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertThat(r1).isNotEqualTo(r2);
	}
}