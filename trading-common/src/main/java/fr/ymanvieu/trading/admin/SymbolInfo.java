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
package fr.ymanvieu.trading.admin;

import fr.ymanvieu.trading.rate.Quote;

public class SymbolInfo {

	private final String code;

	private final String name;

	private final boolean historyFound;

	private final Quote quote;

	public SymbolInfo(String code, String name, boolean historyFound, Quote quote) {
		this.code = code;
		this.name = name;
		this.historyFound = historyFound;
		this.quote = quote;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public boolean isHistoryFound() {
		return historyFound;
	}

	public Quote getQuote() {
		return quote;
	}
}
