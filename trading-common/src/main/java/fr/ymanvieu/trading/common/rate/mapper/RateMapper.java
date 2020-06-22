/**
 * Copyright (C) 2019 Yoann Manvieu
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
package fr.ymanvieu.trading.common.rate.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.entity.RateEntity;
import fr.ymanvieu.trading.common.symbol.mapper.SymbolMapper;

@Mapper(config = MapstructConfig.class, uses = SymbolMapper.class)
public interface RateMapper {
	
	Rate mapToRate(RateEntity entity);
	
	List<Rate> mapToRates(List<? extends RateEntity> entites);
}
