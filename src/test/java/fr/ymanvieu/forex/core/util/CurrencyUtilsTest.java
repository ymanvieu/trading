/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.forex.core.util;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.CHF;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.GBP;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CurrencyUtilsTest {

	@Test
	public void testCodeForCurrency() {
		assertThat(CurrencyUtils.codeForCurrency(USD)).isEqualTo("us");
		assertThat(CurrencyUtils.codeForCurrency(EUR)).isEqualTo("europeanunion");
		assertThat(CurrencyUtils.codeForCurrency("RUB")).isEqualTo("ru");
		assertThat(CurrencyUtils.codeForCurrency(GBP)).isEqualTo("gb");
		assertThat(CurrencyUtils.codeForCurrency(CHF)).isEqualTo("ch");
		assertThat(CurrencyUtils.codeForCurrency("XAU")).isEqualTo("gold");
		assertThat(CurrencyUtils.codeForCurrency("TOTO")).isNull();
	}
}