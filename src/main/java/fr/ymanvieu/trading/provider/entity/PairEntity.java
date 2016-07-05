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
package fr.ymanvieu.trading.provider.entity;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.MoreObjects;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@Entity
@Table(name = "pair")
public class PairEntity {

	@Id
	private String symbol;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "source", referencedColumnName = "code")
	private SymbolEntity source;

	@ManyToOne
	@JoinColumn(name = "target", referencedColumnName = "code")
	private SymbolEntity target;

	@Column(name = "provider_code", nullable = false)
	private String providerCode;

	public PairEntity() {
	}

	public PairEntity(String symbol, String name, String source, String target, String providerCode) {
		this.symbol = requireNonNull(symbol, "symbol is null");
		this.name = requireNonNull(name, "name is null");
		this.source = new SymbolEntity(requireNonNull(source, "source is null"));
		this.target = new SymbolEntity(requireNonNull(target, "target is null"));
		this.providerCode = requireNonNull(providerCode, "providerCode is null");
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}

	public SymbolEntity getSource() {
		return source;
	}

	public SymbolEntity getTarget() {
		return target;
	}

	public String getProviderCode() {
		return providerCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbol, name, source.getCode(), target.getCode(), providerCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof PairEntity))
			return false;

		PairEntity other = (PairEntity) obj;

		return Objects.equals(symbol, other.symbol) //
				&& Objects.equals(name, other.name) //
				&& Objects.equals(source.getCode(), other.source.getCode()) //
				&& Objects.equals(target.getCode(), other.target.getCode()) //
				&& Objects.equals(providerCode, other.providerCode);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("symbol", symbol) //
				.add("name", name) //
				.add("source", source.getCode()) //
				.add("target", target.getCode()) //
				.add("providerCode", providerCode).toString();
	}
}
