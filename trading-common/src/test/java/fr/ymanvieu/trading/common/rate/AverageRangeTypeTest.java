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
