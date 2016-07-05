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

import java.math.BigDecimal;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class OrderInfo {

	private List<SymbolEntity> symbols;

	private Asset selected;
	private Asset selectedCurrency;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private BigDecimal gainCost;

	public OrderInfo(List<SymbolEntity> symbols, Asset selected, Asset selectedCurrency, BigDecimal gainCost) {
		this.symbols = symbols;
		this.selected = selected;
		this.selectedCurrency = selectedCurrency;
		this.gainCost = gainCost;
	}

	public List<SymbolEntity> getSymbols() {
		return symbols;
	}

	public Asset getSelected() {
		return selected;
	}

	public Asset getSelectedCurrency() {
		return selectedCurrency;
	}

	public BigDecimal getGainCost() {
		return gainCost;
	}
}
