package fr.ymanvieu.trading.rate;

import java.math.BigDecimal;
import java.time.Instant;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public interface FavoriteRate {

	Boolean getFavorite();
	
	SymbolEntity getFromcur();
	SymbolEntity getTocur();

	BigDecimal getValue();
	Instant getDate();
}