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
package fr.ymanvieu.trading.symbol;

import com.google.common.base.MoreObjects;

public class Symbol {

	private final String code;

	private final String name;

	private final String countryFlag;

	public Symbol(String code, String name, String countryFlag) {
		this.code = code;
		this.name = name;
		this.countryFlag = countryFlag;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCountryFlag() {
		return countryFlag;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("code", code) //
				.add("name", name) //
				.add("countryFlag", countryFlag).toString();
	}
}