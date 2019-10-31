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
package fr.ymanvieu.trading.test.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

public class DateParserTest {

	@Test
	public void testParse() {
		Instant result = DateParser.parse("2015-04-09T02:15:10.554");
		assertThat(result).isEqualTo(ZonedDateTime.of(LocalDateTime.parse("2015-04-09T02:15:10.554"), ZoneId.systemDefault()).toInstant());
	}
	
	@Test
	public void testParseWithTimeZone() {
		Instant result = DateParser.parse("2015-04-09T00:15:10.554-02:00");
		assertThat(result).isEqualTo("2015-04-09T02:15:10.554Z");
	}
}