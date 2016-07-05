/**
 *	Copyright (C) 2016	Yoann Manvieu
 *
 *	This software is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.symbol.util;

import java.util.List;

import fr.ymanvieu.trading.symbol.Symbol;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

public class SymbolUtils {

	public static Symbol convert(SymbolEntity se) {
		return new Symbol(se.getCode(), se.getName(), se.getCountryFlag());
	}
	
	public static SymbolEntity getFromList(String code, List<SymbolEntity> symbols) {
		for (SymbolEntity se : symbols) {
			if (se.getCode().equals(code)) {
				return se;
			}
		}

		return null;
	}
}