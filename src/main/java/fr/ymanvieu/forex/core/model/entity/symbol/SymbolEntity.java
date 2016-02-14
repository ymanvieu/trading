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
package fr.ymanvieu.forex.core.model.entity.symbol;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;

import fr.ymanvieu.forex.core.model.entity.rate.LatestRate;

@Entity
@Table(name = "symbols")
public class SymbolEntity {

	@JsonIgnore
	@Id
	@Column(columnDefinition = "integer(11)")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "varchar(8)", nullable = false)
	private String code;

	@Column(columnDefinition = "varchar(3)", nullable = false)
	private String currency;

	@JsonIgnoreProperties({ "fromcur", "tocur" })
	@OneToOne
	@JoinColumns({ //
			@JoinColumn(insertable = false, updatable = false, referencedColumnName = "fromcur", name = "code"), //
			@JoinColumn(insertable = false, updatable = false, referencedColumnName = "tocur", name = "currency") })
	private LatestRate latestrate;

	public SymbolEntity() {
	}

	public SymbolEntity(String code, String currency, String name) {
		this.code = requireNonNull(code, "code is null");
		this.name = requireNonNull(name, "name is null");
		this.currency = requireNonNull(currency, "currency is null");
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getCurrency() {
		return currency;
	}

	public LatestRate getLatestrate() {
		return latestrate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof SymbolEntity))
			return false;

		SymbolEntity other = (SymbolEntity) obj;

		return Objects.equals(code, other.code) //
				&& Objects.equals(name, other.name) //
				&& Objects.equals(currency, other.currency);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("code", code) //
				.add("name", name) //
				.add("currency", currency).toString();
	}
}
