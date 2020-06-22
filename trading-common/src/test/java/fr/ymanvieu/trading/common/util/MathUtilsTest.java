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
package fr.ymanvieu.trading.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

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
