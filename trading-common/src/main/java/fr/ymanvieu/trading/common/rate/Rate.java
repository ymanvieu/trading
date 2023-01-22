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
package fr.ymanvieu.trading.common.rate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.ymanvieu.trading.common.symbol.Symbol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
public class Rate {

	private Symbol fromcur;
	private Symbol tocur;
	private BigDecimal value;
	private Instant date;

	@Override
	public int hashCode() {
		return Objects.hash(fromcur, tocur, date, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Rate))
			return false;

		Rate other = (Rate) obj;

		return Objects.equals(fromcur, other.fromcur) //
				&& Objects.equals(tocur, other.tocur) //
				&& Objects.equals(date, other.date) //
				&& value.compareTo(other.value) == 0;
	}
}