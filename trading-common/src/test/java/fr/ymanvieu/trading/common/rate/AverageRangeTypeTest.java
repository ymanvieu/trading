package fr.ymanvieu.trading.common.rate;

import static fr.ymanvieu.trading.common.rate.AverageRangeType.DAY;
import static fr.ymanvieu.trading.common.rate.AverageRangeType.HOUR;
import static fr.ymanvieu.trading.common.rate.AverageRangeType.NONE;
import static fr.ymanvieu.trading.common.rate.AverageRangeType.WEEK;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AverageRangeTypeTest {

	private static Object[][] testGetRange() {
		return new Object[][] {
				{ null, null, WEEK },
				{ "2013-08-01T00:05:00Z", "2015-08-01T00:05:00Z", WEEK },
				{ "2015-02-01T00:05:00Z", "2015-08-01T00:05:00Z", DAY },
				{ "2015-02-01T00:05:00Z", "2015-08-01T00:03:00Z", HOUR },
				{ "2015-02-10T00:05:00Z", "2015-02-17T00:05:00Z", HOUR },
				{ "2015-02-10T00:05:00Z", "2015-02-17T00:04:00Z", NONE },
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testGetRange(Instant start, Instant end, AverageRangeType expected) {
		assertThat(AverageRangeType.getRange(start, end)).isEqualTo(expected);
	}
}
