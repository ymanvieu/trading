/**
 * Copyright (C) 2016 Yoann Manvieu
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

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

	private static final BigDecimal HUNDRED = new BigDecimal("100");
	private static final int SCALE = 10;
	
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return dividend.divide(divisor, SCALE, RoundingMode.HALF_EVEN);
	}

	/**
	 * Divide 1 by the specified BigDecimal.
	 * 
	 * @param d
	 * @return the divided BigDecimal
	 */
	public static BigDecimal invert(BigDecimal d) {
		return divide(BigDecimal.ONE, d);
	}

	public static float percentChange(BigDecimal oldValue, BigDecimal newValue) {
		return divide(newValue.subtract(oldValue), oldValue).multiply(HUNDRED).floatValue();
	}

	public static boolean equalsByComparingTo(BigDecimal bd1, BigDecimal bd2) {
		return bd1.compareTo(bd2) == 0;
	}
}
