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
package fr.ymanvieu.trading.portofolio.dto;

import static fr.ymanvieu.trading.portofolio.util.NumberFormatUtils.NUMBER_PATTERN;
import static fr.ymanvieu.trading.portofolio.util.NumberFormatUtils.NUMBER_WITH_SIGN_PATTERN;

import org.springframework.format.annotation.NumberFormat;

import fr.ymanvieu.trading.symbol.dto.SymbolDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AssetDTO {

	private SymbolDTO symbol;

	private SymbolDTO currency;

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
}