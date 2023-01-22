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
