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
package fr.ymanvieu.trading.util;

import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void testNextDay() {
		Date date = parse("2015-09-12T18:50:26+02:00");

		Date result = DateUtils.nextDay(date);

		assertThat(result).hasSameTimeAs(parse("2015-09-13T18:50:26+02:00"));
	}

	@Test
	public void testGetNbOfMonths() {
		Date date1 = parse("2015-02-05T18:50:26+01:00");
		Date date2 = parse("2015-08-05T18:50:26+02:00");

		int result = DateUtils.getNbOfMonths(date1, date2);

		assertThat(result).isEqualTo(6);
	}

	@Test
	public void testGetNbOfMonths_JustBelowSixMonths() {
		Date date1 = parse("2015-02-05T18:50:26.556+01:00");
		Date date2 = parse("2015-08-05T18:50:26.555+02:00");

		int result = DateUtils.getNbOfMonths(date1, date2);

		assertThat(result).isEqualTo(5);
	}

	@Test
	public void testGetNbOfMonths_DatesfromDifferentYears() {
		Date date1 = parse("2014-12-31T18:50:26+01:00");
		Date date2 = parse("2015-01-01T18:50:26+01:00");

		int result = DateUtils.getNbOfMonths(date1, date2);

		assertThat(result).isEqualTo(0);
	}
}