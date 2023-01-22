package fr.ymanvieu.trading.common.admin;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class AdminException extends BusinessException {

	private static final long serialVersionUID = 3847358680371605765L;

	private AdminException(String key, Object... args) {
		super(key, args);
	}
	
	public static AdminException currencyAlreadyExists(String code) {
		return new AdminException("admin.error.currency-already-exists", code);
	}

    public static AdminException alreadyExistsWithOtherCurrency(String symbol, String existingCurrency) {
		return new AdminException("admin.error.already-exists-with-other-currency", symbol, existingCurrency);
    }
}
