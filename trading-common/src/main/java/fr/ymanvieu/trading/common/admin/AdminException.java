package fr.ymanvieu.trading.common.admin;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class AdminException extends BusinessException {

	private AdminException(String key, Object... args) {
		super(key, args);
	}
	
	public static AdminException currencyAlreadyExists(String currencyCode) {
		return new AdminException("admin.error.currency-already-exists", currencyCode);
	}

    public static AdminException symbolAlreadyExistsWithOtherCurrency(String symbol, String existingCurrency) {
		return new AdminException("admin.error.symbol-already-exists-with-other-currency", symbol, existingCurrency);
    }
}
