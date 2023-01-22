package fr.ymanvieu.trading.common.symbol;

import java.util.List;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class SymbolException extends BusinessException {

	private static final long serialVersionUID = -1017587715818305285L;

	private SymbolException(String key, Object... args) {
		super(key, args);
	}

	public static SymbolException UNKNOWN(String code) {
		return new SymbolException("symbols.error.unknown", code);
	}

	public static SymbolException alreadyExists(String code) {
		return new SymbolException("symbols.error.already_exists", code);
	}

	public static SymbolException UNAVAILABLE(String code) {
		return new SymbolException("symbols.error.unavailable", code);
	}

	public static SymbolException USED_AS_CURRENCY(String code, List<String> codes) {
		return new SymbolException("symbols.error.currency_constraint", new Object[] { code, codes });
	}
}
