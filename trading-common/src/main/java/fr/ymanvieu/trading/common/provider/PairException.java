package fr.ymanvieu.trading.common.provider;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class PairException extends BusinessException {

	private static final long serialVersionUID = 8715490540458309882L;

	private PairException(String key, Object... args) {
		super(key, args);
	}

	public static PairException alreadyExists(String symbol, String provider) {
		return new PairException("pair.error.already_exists", symbol, provider);
	}
	
	public static PairException currencyNotFound(String code) {
		return new PairException("pair.error.currency-not-found", code);
	}

	public static PairException notFound(Integer id) {
		return new PairException("pair.error.not-found", id);
	}
}
