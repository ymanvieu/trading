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

import static fr.ymanvieu.trading.portofolio.util.NumberFormatUtils.NUMBER_WITH_SIGN_PATTERN;

import java.math.BigDecimal;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfoDTO {

	private AssetDTO selected;
	private AssetDTO selectedCurrency;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private BigDecimal gainCost;	
}
