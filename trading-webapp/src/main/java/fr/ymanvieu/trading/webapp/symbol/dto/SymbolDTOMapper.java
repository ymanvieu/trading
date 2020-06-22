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
package fr.ymanvieu.trading.webapp.symbol.dto;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.symbol.Symbol;

@Mapper(config = MapstructConfig.class)
public interface SymbolDTOMapper {

	SymbolDTO toDto(Symbol symbol);

	List<SymbolDTO> toDto(List<Symbol> symbols);
	
	SymbolDTO toDto(FavoriteRate.Symbol symbol);
}
