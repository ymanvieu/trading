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
package fr.ymanvieu.trading.rate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import com.google.common.base.MoreObjects;

public class Quote {

	private String code;
	private String currency;

	private BigDecimal price;
	private Date time;

	public Quote(String code, BigDecimal price, Date time) {
		this.code = code;
		this.price = price;
		this.time = time;
	}

	public Quote(String code, String currency, BigDecimal price, Date time) {
		this.code = code;
		this.currency = currency;
		this.price = price;
		this.time = time;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public Date getTime() {
		return time;
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

		return time.compareTo(other.time) == 0 //
				&& Objects.equals(currency, other.currency) //
				&& Objects.equals(code, other.code) //
				&& price.compareTo(other.price) == 0;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("code", code) //
				.add("currency", currency) //
				.add("price", price) //
				.add("time", time).toString();
	}
}
