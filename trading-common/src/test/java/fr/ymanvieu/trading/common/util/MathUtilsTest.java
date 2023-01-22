package fr.ymanvieu.trading.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class MathUtilsTest {

	@Test
	public void testDivide() {
		assertThat(MathUtils.divide(new BigDecimal("1.2"), new BigDecimal("0.7"))).isEqualByComparingTo("1.7142857143");
	}

	@Test
	public void testInvert() {
		assertThat(MathUtils.invert(new BigDecimal("2.3"))).isEqualByComparingTo("0.4347826087");
	}

	@Test
	public void testPercentChange() {
		assertThat(MathUtils.percentChange(new BigDecimal("100"), new BigDecimal("110"))).isEqualTo(10f);
	}
}
