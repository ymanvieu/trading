/**
 * Copyright (C) 2014 Yoann Manvieu
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
package fr.ymanvieu.forex.core.model.entity.rate;

import java.io.Serializable;
import java.util.Objects;

import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;

public class RateEntityId implements Serializable {

	private static final long serialVersionUID = -6574992688912894735L;

	private SymbolEntity fromcur;

	private SymbolEntity tocur;

	public SymbolEntity getFromcur() {
		return fromcur;
	}
	
	public void setFromcur(SymbolEntity fromcur) {
		this.fromcur = fromcur;
	}

	public SymbolEntity getTocur() {
		return tocur;
	}
	
	public void setTocur(SymbolEntity tocur) {
		this.tocur = tocur;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fromcur.getCode(), tocur.getCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof RateEntityId))
			return false;

		RateEntityId other = (RateEntityId) obj;

		return Objects.equals(fromcur.getCode(), other.fromcur.getCode()) //
				&& Objects.equals(tocur.getCode(), other.tocur.getCode());
	}
}
