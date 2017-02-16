/**
 * Copyright (C) 2016 Yoann Manvieu
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
package fr.ymanvieu.trading.provider;

import fr.ymanvieu.trading.exception.BusinessException;

public class ProviderException extends BusinessException {

	private static final long serialVersionUID = 879506415640327106L;

	private ProviderException(String key, Object... args) {
		super(key, args);
	}

	public static ProviderException currencyNotFound(String code) {
		return new ProviderException("provider.error.currency_not_found", code);
	}
}