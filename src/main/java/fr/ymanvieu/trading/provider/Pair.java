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

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects;

import fr.ymanvieu.trading.symbol.Symbol;

public class Pair {

	private final String symbol;
	private final String name;
	private final Symbol source;
	private final Symbol target;
	private final String provider;

	public Pair(String symbol, String name, Symbol source, Symbol target, String provider) {
		this.symbol = requireNonNull(symbol, "symbol is null");
		this.name = requireNonNull(name, "name is null");
		this.source = requireNonNull(source, "source is null");
		this.target = requireNonNull(target, "target is null");
		this.provider = requireNonNull(provider, "provider is null");
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public Symbol getSource() {
		return source;
	}

	public Symbol getTarget() {
		return target;
	}

	public String getProvider() {
		return provider;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("symbol", symbol) //
				.add("name", name) //
				.add("source", source) //
				.add("target", target) //
				.add("provider", provider).toString();
	}
}