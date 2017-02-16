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
package fr.ymanvieu.trading.portofolio;

import com.google.common.base.MoreObjects;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class Order {

	private final SymbolEntity from;

	private final float quantity;

	private final SymbolEntity to;

	private final float value;

	public Order(SymbolEntity from, float quantity, SymbolEntity to, float value) {
		this.from = from;
		this.quantity = quantity;
		this.to = to;
		this.value = value;
	}

	public SymbolEntity getFrom() {
		return from;
	}

	public float getQuantity() {
		return quantity;
	}

	public SymbolEntity getTo() {
		return to;
	}

	public float getValue() {
		return value;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("from", from) //
				.add("quantity", quantity) //
				.add("to", to) //
				.add("value", value).toString();
	}
}
