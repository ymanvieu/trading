package fr.ymanvieu.trading.rate;

import java.util.Date;

import fr.ymanvieu.trading.util.DateUtils;

public enum AverageRangeType {
	NONE,
	HOUR,
	DAY,
	WEEK;

	private static final long A_WEEK_IN_MS = 7 * 24 * 3600 * 1000L;

	public static AverageRangeType getRange(Date start, Date endDate) {
		if (start == null || endDate == null) {
			return WEEK;
		}

		long timeRangeInMs = endDate.getTime() - start.getTime();

		int nbOfMonths = DateUtils.getNbOfMonths(start, endDate);

		if (nbOfMonths >= 24) {
			return WEEK;
		}

		if (nbOfMonths >= 6) {
			return DAY;
		}

		if (timeRangeInMs >= A_WEEK_IN_MS) {
			return HOUR;
		}

		return NONE;
	}
}