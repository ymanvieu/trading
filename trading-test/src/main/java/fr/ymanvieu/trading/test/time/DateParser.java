/**
 * Copyright (C) 2017 Yoann Manvieu
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class DateParser {

	private static final DateTimeFormatter DTF = DateTimeFormatter.ISO_DATE_TIME;

	/**
	 * Parses a string as ISO format with optional zone offset (system default if no offset).<br>
	 * e.g. 2015-04-09T02:10:28, 2012-12-21T00:00:00+01:00
	 * 
	 * @return the corresponding date object
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 * @see ZoneId#systemDefault
	 */
	public static Date parse(String dateStr) {
		TemporalAccessor temporalAccessor = DTF.parseBest(dateStr, ZonedDateTime::from, LocalDateTime::from);
		
	    if (temporalAccessor instanceof LocalDateTime) {
	    	temporalAccessor = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault());
	    }

		return Date.from(Instant.from(temporalAccessor));
	}
}
