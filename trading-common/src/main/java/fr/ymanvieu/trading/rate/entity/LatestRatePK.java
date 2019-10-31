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
package fr.ymanvieu.trading.rate.entity;

import java.io.Serializable;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import lombok.Data;

@Data
public class LatestRatePK implements Serializable {
	
	private static final long serialVersionUID = -5231047284548792369L;
	
	private SymbolEntity fromcur;
	private SymbolEntity tocur;
}
