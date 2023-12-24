package fr.ymanvieu.trading.test.time;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

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
