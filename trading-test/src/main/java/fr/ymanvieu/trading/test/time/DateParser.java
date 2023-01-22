package fr.ymanvieu.trading.test.time;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
	
public class DateParser {

	/**
	 * Parses a string as ISO format with optional zone offset (system default if no offset).<br>
	 * e.g. 2015-04-09T02:10:28, 2012-12-21T00:00:00+01:00
	 * 
	 * @return the corresponding date object
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 * @see ZoneId#systemDefault
	 */
	public static Instant parse(String dateStr) {
		TemporalAccessor temporalAccessor = ISO_DATE_TIME.parseBest(dateStr, ZonedDateTime::from, LocalDateTime::from);
		
	    if (temporalAccessor instanceof LocalDateTime) {
	    	return ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
	    } else {
	    	return temporalAccessor.query(ZonedDateTime::from).toInstant();
	    }
	}
}
