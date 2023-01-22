package fr.ymanvieu.trading.common.util;

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

	public static double percentChange(BigDecimal oldValue, BigDecimal newValue) {
		return divide(newValue.subtract(oldValue), oldValue).multiply(HUNDRED).doubleValue();
	}

	public static boolean equalsByComparingTo(BigDecimal bd1, BigDecimal bd2) {
		return bd1.compareTo(bd2) == 0;
	}
}
