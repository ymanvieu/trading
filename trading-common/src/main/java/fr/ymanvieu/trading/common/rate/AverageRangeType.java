package fr.ymanvieu.trading.common.rate;

import static java.time.ZoneOffset.UTC;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public enum AverageRangeType {
	NONE,
	HOUR,
	DAY,
	WEEK;

	private static final long A_WEEK_IN_S = 7 * 24 * 3600;

	public static AverageRangeType getRange(Instant start, Instant endDate) {
		if (start == null || endDate == null) {
			return WEEK;
		}

		int nbOfMonths = (int) ChronoUnit.MONTHS.between(start.atOffset(UTC), endDate.atOffset(UTC));

		if (nbOfMonths >= 24) {
			return WEEK;
		}

		if (nbOfMonths >= 6) {
			return DAY;
		}

		long timeRangeInMs = endDate.getEpochSecond() - start.getEpochSecond();
		
		if (timeRangeInMs >= A_WEEK_IN_S) {
			return HOUR;
		}

		return NONE;
	}
}
