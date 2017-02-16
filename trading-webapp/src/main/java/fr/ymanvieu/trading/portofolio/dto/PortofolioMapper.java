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

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.ymanvieu.trading.portofolio.AssetInfo;
import fr.ymanvieu.trading.portofolio.OrderInfo;
import fr.ymanvieu.trading.portofolio.Portofolio;
import fr.ymanvieu.trading.symbol.dto.SymbolMapper;

@Mapper(uses = SymbolMapper.class)
public interface PortofolioMapper {

	PortofolioMapper MAPPER = Mappers.getMapper(PortofolioMapper.class);

	AssetDTO toAssetDto(AssetInfo p);

	PortofolioDTO toPortofolioDto(Portofolio p);

	OrderInfoDTO toOrderInfoDto(OrderInfo oi);
}