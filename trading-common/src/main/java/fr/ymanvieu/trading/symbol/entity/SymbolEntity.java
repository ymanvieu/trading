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
package fr.ymanvieu.trading.symbol.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Entity
@Table(name = "symbols")
public class SymbolEntity {

	@Id
	@Column(length = 8)
	private String code;

	private String name;

	@Column(name = "country_flag", length = 16)
	private String countryFlag;

	@ManyToOne
	@JoinColumn(name = "currency", referencedColumnName = "code")
	private SymbolEntity currency;

	protected SymbolEntity() {
	}

	public SymbolEntity(String code) {
		Objects.requireNonNull(code, "code is null");
		Preconditions.checkArgument(code.length() <= 8, "code size is more than 8: ", code);

		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountryFlag() {
		return countryFlag;
	}

	public void setCountryFlag(String countryFlag) {
		this.countryFlag = countryFlag;
	}

	public SymbolEntity getCurrency() {
		return currency;
	}

	public void setCurrency(SymbolEntity currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, currency, name, countryFlag);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof SymbolEntity))
			return false;

		SymbolEntity other = (SymbolEntity) obj;

		return Objects.equals(code, other.code) //
				&& Objects.equals(currency, other.currency) //
				&& Objects.equals(name, other.name) //
				&& Objects.equals(countryFlag, other.countryFlag);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("code", code) //
				.add("currency", currency) //
				.add("name", name) //
				.add("countryFlag", countryFlag).toString();
	}
}
