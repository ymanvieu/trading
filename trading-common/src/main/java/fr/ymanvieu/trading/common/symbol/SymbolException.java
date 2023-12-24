package fr.ymanvieu.trading.common.symbol;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class SymbolException extends BusinessException {

	private SymbolException(String key, Object... args) {
		super(key, args);
	}

	public static SymbolException unknown(String code) {
		return new SymbolException("symbol.error.unknown", code);
	}

	public static SymbolException alreadyExists(String code) {
		return new SymbolException("symbol.error.already_exists", code);
	}

	public static SymbolException unavailable(String code) {
		return new SymbolException("symbol.error.unavailable", code);
	}
}
