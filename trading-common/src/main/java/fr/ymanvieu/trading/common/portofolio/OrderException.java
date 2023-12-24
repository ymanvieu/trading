package fr.ymanvieu.trading.common.portofolio;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class OrderException extends BusinessException {

	private OrderException(String key, Object... args) {
		super(key, args);
	}

	public static OrderException notEnoughFund(String code, double quantity, String curCode, double available, double needed) {
		return new OrderException("order.error.not_enough_fund", quantity, code, curCode, available, needed);
	}

	public static OrderException notEnoughOwned(String code, double owned, double toSell) {
		return new OrderException("order.error.not_enough_owned", code, owned, toSell);
	}

	public static OrderException noQuantityOwned(String code) {
		return new OrderException("order.error.no_quantity_owned", code);
	}
}
