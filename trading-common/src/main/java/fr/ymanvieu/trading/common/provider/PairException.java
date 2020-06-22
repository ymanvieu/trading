/**
 * Copyright (C) 2019 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

	public static PairException notFound(String symbol, String providerCode) {
		return new PairException("pair.error.not-found", symbol, providerCode);
	}
}
