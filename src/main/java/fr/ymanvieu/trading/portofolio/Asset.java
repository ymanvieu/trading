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

import static fr.ymanvieu.trading.util.MathUtils.NUMBER_PATTERN;
import static fr.ymanvieu.trading.util.MathUtils.NUMBER_WITH_SIGN_PATTERN;

import org.springframework.format.annotation.NumberFormat;

import fr.ymanvieu.trading.symbol.Symbol;

public class Asset {

	private final Symbol symbol;

	private final Symbol currency;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private Float quantity;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private Float value;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private Float currentValue;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private Float currentRate;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private Float percentChange;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private Float valueChange;

	public Asset(Symbol symbol, Symbol currency, float quantity) {
		this.symbol = symbol;
		this.currency = currency;
		this.quantity = quantity;
	}

	public Asset(Symbol symbol, Symbol currency) {
		this.symbol = symbol;
		this.currency = currency;
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public Symbol getCurrency() {
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