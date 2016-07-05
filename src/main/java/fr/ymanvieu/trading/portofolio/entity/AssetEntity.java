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
package fr.ymanvieu.trading.portofolio.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.google.common.base.MoreObjects;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.user.entity.UserEntity;

@Entity
@Table(name = "assets", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "symbol_code" }))
public class AssetEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "symbol_code")
	private SymbolEntity symbol;

	@Column(precision = 20, scale = 10, nullable = false)
	private BigDecimal quantity;

	@Column(name = "total_price", precision = 20, scale = 10, nullable = false)
	private BigDecimal totalPrice;

	@Version
	@Column(name = "last_update", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	public AssetEntity() {
	}

	public AssetEntity(UserEntity user, SymbolEntity symbol, BigDecimal quantity, BigDecimal totalPrice) {
		this.user = user;
		this.symbol = symbol;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
	}

	public UserEntity getUser() {
		return user;
	}

	public SymbolEntity getSymbol() {
		return symbol;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, symbol, quantity, totalPrice);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || !(obj instanceof AssetEntity))
			return false;

		AssetEntity other = (AssetEntity) obj;

		return Objects.equals(user, other.user) //
				&& Objects.equals(symbol, other.symbol) //
				&& Objects.equals(quantity, other.quantity) //
				&& Objects.equals(totalPrice, other.totalPrice);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("user", user) //
				.add("symbol", symbol) //
				.add("quantity", quantity) //
				.add("totalPrice", totalPrice).toString();
	}
}
