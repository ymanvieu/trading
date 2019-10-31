/**
 *	Copyright (C) 2016	Yoann Manvieu
 *
 *	This software is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.portofolio;

import fr.ymanvieu.trading.exception.BusinessException;

public class OrderException extends BusinessException {

	private static final long serialVersionUID = 1465687868378906633L;

	private OrderException(String key, Object... args) {
		super(key, args);
	}

	public static OrderException NOT_ENOUGH_FUND(String code, double quantity, String curCode, double available, double needed) {
		return new OrderException("order.error.not_enough_fund", quantity, code, curCode, available, needed);
	}

	public static OrderException NOT_ENOUGH_OWNED(String code, double owned, double toSell) {
		return new OrderException("order.error.not_enough_owned", code, owned, toSell);
	}

	public static OrderException NO_QUANTITY_OWNED(String code) {
		return new OrderException("order.error.no_quantity_owned", code);
	}
}