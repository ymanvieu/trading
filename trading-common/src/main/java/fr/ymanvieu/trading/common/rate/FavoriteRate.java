package fr.ymanvieu.trading.common.rate;

import java.math.BigDecimal;
import java.time.Instant;

public interface FavoriteRate {

	Boolean getFavorite();
	
	Symbol getFromcur();
	Symbol getTocur();

	BigDecimal getValue();
	Instant getDate();
	
	interface Symbol {
		String getCode();
		String getName();
		String getCountryFlag();
	}
}