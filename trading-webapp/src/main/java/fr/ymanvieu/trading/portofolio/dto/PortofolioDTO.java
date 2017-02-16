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

import java.util.List;

import org.springframework.format.annotation.NumberFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PortofolioDTO {

	private AssetDTO baseCurrency;

	private List<AssetDTO> assets;

	@NumberFormat(pattern = NUMBER_PATTERN)
	private float currentValue;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private float percentChange;

	@NumberFormat(pattern = NUMBER_WITH_SIGN_PATTERN)
	private float valueChange;
}
