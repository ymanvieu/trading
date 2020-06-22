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
package fr.ymanvieu.trading.common.portofolio;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AssetInfo {

	private final SymbolEntity symbol;
	private final SymbolEntity currency;

	private Double quantity;
	private Double value;
	private Double currentValue;
	private Double currentRate;
	private Double percentChange;
	private Double valueChange;

	public AssetInfo(SymbolEntity symbol, SymbolEntity currency, double quantity) {
		this.symbol = symbol;
		this.currency = currency;
		this.quantity = quantity;
	}
}