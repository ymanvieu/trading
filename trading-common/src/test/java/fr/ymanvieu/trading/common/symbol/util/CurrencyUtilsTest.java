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
package fr.ymanvieu.trading.common.symbol.util;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.CHF;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class CurrencyUtilsTest {

	@Test
	public void testCountryFlagForCurrency() {
		assertThat(CurrencyUtils.countryFlagForCurrency(USD)).isEqualTo("us");
		assertThat(CurrencyUtils.countryFlagForCurrency(EUR)).isEqualTo("eu");
		assertThat(CurrencyUtils.countryFlagForCurrency("RUB")).isEqualTo("ru");
		assertThat(CurrencyUtils.countryFlagForCurrency(GBP)).isEqualTo("gb");
		assertThat(CurrencyUtils.countryFlagForCurrency(CHF)).isEqualTo("ch");
		assertThat(CurrencyUtils.countryFlagForCurrency("XAU")).isEqualTo("gold");
		assertThat(CurrencyUtils.countryFlagForCurrency("TOTO")).isNull();
	}

	@Test
	public void testNameForCurrency() {
		assertThat(CurrencyUtils.nameForCurrency(USD)).isEqualTo("US Dollar");
		assertThat(CurrencyUtils.nameForCurrency(EUR)).isEqualTo("Euro");
		assertThat(CurrencyUtils.nameForCurrency(GBP)).isEqualTo("British Pound");
		assertThat(CurrencyUtils.nameForCurrency("XAU")).isEqualTo("Gold");
		assertThat(CurrencyUtils.nameForCurrency("XAF")).isEqualTo("Central African CFA Franc");
		assertThat(CurrencyUtils.nameForCurrency("XAG")).isEqualTo("Silver");
		assertThat(CurrencyUtils.nameForCurrency("TOTO")).isNull();
	}
}