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
package fr.ymanvieu.trading.provider;

import java.util.List;

import fr.ymanvieu.trading.provider.entity.PairEntity;

public class PairsResult {

	private final List<PairEntity> existingPairs;
	private final List<LookupInfo> availableSymbols;
	
	public PairsResult(List<PairEntity> existingPairs, List<LookupInfo> availableSymbols) {
		this.existingPairs = existingPairs;
		this.availableSymbols = availableSymbols;
	}
	
	public List<PairEntity> getExistingPairs() {
		return existingPairs;
	}
	
	public List<LookupInfo> getAvailableSymbols() {
		return availableSymbols;
	}
}
