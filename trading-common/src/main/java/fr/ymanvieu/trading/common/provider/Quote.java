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
package fr.ymanvieu.trading.common.provider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Quote {

	private String code;
	private String currency;

	private final BigDecimal price;
	private final Instant time;

	public Quote(String code, BigDecimal price, Instant time) {
		this.code = code;
		this.price = price;
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, currency, price, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof Quote))
			return false;

		Quote other = (Quote) obj;

		return Objects.equals(time,other.time) //
				&& Objects.equals(currency, other.currency) //
				&& Objects.equals(code, other.code) //
				&& price.compareTo(other.price) == 0;
	}
}
