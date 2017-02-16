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

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class AssetInfo {

	private final SymbolEntity symbol;

	private final SymbolEntity currency;

	private Float quantity;

	private Float value;

	private Float currentValue;

	private Float currentRate;

	private Float percentChange;

	private Float valueChange;

	public AssetInfo(SymbolEntity symbol, SymbolEntity currency, float quantity) {
		this.symbol = symbol;
		this.currency = currency;
		this.quantity = quantity;
	}

	public AssetInfo(SymbolEntity symbol, SymbolEntity currency) {
		this.symbol = symbol;
		this.currency = currency;
	}

	public SymbolEntity getSymbol() {
		return symbol;
	}

	public SymbolEntity getCurrency() {
		return currency;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Float getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(float currentValue) {
		this.currentValue = currentValue;
	}

	public Float getCurrentRate() {
		return currentRate;
	}

	public void setCurrentRate(float currentRate) {
		this.currentRate = currentRate;
	}

	public Float getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(float percentChange) {
		this.percentChange = percentChange;
	}

	public Float getValueChange() {
		return valueChange;
	}

	public void setValueChange(float valueChange) {
		this.valueChange = valueChange;
	}
}