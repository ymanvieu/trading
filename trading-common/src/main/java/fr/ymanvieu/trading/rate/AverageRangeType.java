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
package fr.ymanvieu.trading.rate;

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