package fr.ymanvieu.trading.common.symbol.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CurrencyUtils {

	public static final String EUR = "EUR";
	public static final String USD = "USD";
	public static final String GBP = "GBP";
	public static final String CHF = "CHF";

	private static final Map<String, String> COMMODITIES = new HashMap<>();

	static {
		COMMODITIES.put("XAU", "gold");
		COMMODITIES.put("XAG", "silver");
	}

	private static List<String> countryCodesForCurrency(String currency) {
		Set<String> codes = new HashSet<>();

		if (currencyFromCode(currency) != null) {
			for (Locale l : Locale.getAvailableLocales()) {
				try {
					Currency currentCurrency = Currency.getInstance(l);

					if (currentCurrency != null && currentCurrency.getCurrencyCode().equals(currency)) {
						codes.add(l.getCountry());
					}
				} catch (IllegalArgumentException e) {
				}
			}
		}

		return new ArrayList<>(codes);
	}

	private static Currency currencyFromCode(String currency) {
		try {
			return Currency.getInstance(currency);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static String countryFlagForCurrency(String c) {
		if (EUR.equals(c)) {
			return "eu";
		} else if (USD.equals(c)) {
			return "us";
		}

		String commodityCode = COMMODITIES.get(c);

		if (commodityCode != null) {
			return commodityCode;
		}

		List<String> codes = countryCodesForCurrency(c);

		if (!codes.isEmpty()) {
			Collections.sort(codes);
			String code = codes.get(0);
			return code.toLowerCase();
		}

		return null;
	}

	public static String nameForCurrency(String currency) {
		Currency cy = currencyFromCode(currency);
		return cy == null ? null : cy.getDisplayName(Locale.ENGLISH);
	}
}
