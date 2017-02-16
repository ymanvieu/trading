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

public class LookupInfo {

	private String code;
	private String name;
	private String exchange;
	private String type;
	private String provider;

	public LookupInfo(String code, String name, String exchange, String type, String provider) {
		this.code = code;
		this.name = name;
		this.exchange = exchange;
		this.type = type;
		this.provider = provider;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getExchange() {
		return exchange;
	}

	public String getType() {
		return type;
	}

	public String getProvider() {
		return provider;
	}
}