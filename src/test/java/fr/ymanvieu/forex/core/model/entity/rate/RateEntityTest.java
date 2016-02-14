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
package fr.ymanvieu.forex.core.model.entity.rate;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.GBP;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

public class RateEntityTest {

	/** 2014-12-19 15:00:00 Europe/Paris */
	private static final long EPOCH_20141219_15H00S00 = 1418997600000l;
	/** 2014-12-18 15:00:00 Europe/Paris */
	private static final long EPOCH_20141218_15H00S00 = 1418911200000l;

	@Test
	public void testEqualsOK() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);
		BigDecimal v1 = new BigDecimal("1.54654654654665465");
		BigDecimal v2 = new BigDecimal("1.54654654654665465");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertEquals(r1, r2);
	}

	@Test
	public void testEqualsOK_SameObject() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		BigDecimal v1 = BigDecimal.valueOf(1.5f);

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);

		assertEquals(r1, r1);
	}

	@Test
	public void testEqualsKO_OppositeCurrencies() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(USD, GBP, v2, d2);

		assertNotEquals(r1, r2);
	}

	@Test
	public void testEqualsKO_DifferentDates() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141219_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertNotEquals(r1, r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesFrom() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(EUR, USD, v2, d2);

		assertNotEquals(r1, r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrenciesTo() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, EUR, v2, d2);

		assertNotEquals(r1, r2);
	}

	@Test
	public void testEqualsKO_DifferentCurrencies() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.5");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(USD, EUR, v2, d2);

		assertNotEquals(r1, r2);
	}

	@Test
	public void testEqualsKO_DifferentValue() {
		Date d1 = new Date(EPOCH_20141218_15H00S00);
		Date d2 = new Date(EPOCH_20141218_15H00S00);

		BigDecimal v1 = new BigDecimal("1.5");
		BigDecimal v2 = new BigDecimal("1.6");

		RateEntity r1 = new RateEntity(GBP, USD, v1, d1);
		RateEntity r2 = new RateEntity(GBP, USD, v2, d2);

		assertNotEquals(r1, r2);
	}
}
