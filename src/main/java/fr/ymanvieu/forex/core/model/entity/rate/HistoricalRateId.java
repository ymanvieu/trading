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

import java.util.Date;
import java.util.Objects;

import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;

public class HistoricalRateId extends RateEntityId {

	private static final long serialVersionUID = -1744069479890521931L;
	
	private Date date;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public SymbolEntity getFromcur() {
		return super.getFromcur();
	}
	
	@Override
	public SymbolEntity getTocur() {
		return super.getTocur();
	}

	@Override
	public int hashCode() {
		return super.hashCode() + Objects.hash(date);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof HistoricalRateId))
			return false;

		HistoricalRateId other = (HistoricalRateId) obj;

		return super.equals(other) && Objects.equals(date, other.date);
	}
}
