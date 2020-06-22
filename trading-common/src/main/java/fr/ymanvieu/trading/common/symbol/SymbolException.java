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
