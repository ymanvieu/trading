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

import static fr.ymanvieu.trading.util.MathUtils.divide;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@Entity
@Table(name = "assets", uniqueConstraints = @UniqueConstraint(columnNames = { "portofolio_id", "symbol_code" }))
public class AssetEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "portofolio_id", nullable = false)
	private PortofolioEntity portofolio;

	@ManyToOne
	@JoinColumn(name = "symbol_code", nullable = false)
	private SymbolEntity symbol;

	@Column(precision = 20, scale = 10, nullable = false)
	private BigDecimal quantity;

	@ManyToOne
	@JoinColumn(name = "currency_code", nullable = false)
	private SymbolEntity currency;

	@Column(name = "currency_amount", precision = 20, scale = 10, nullable = false)
	private BigDecimal currencyAmount;

	@Version
	private long version;

	protected AssetEntity() {
	}

	public AssetEntity(PortofolioEntity portofolio, SymbolEntity symbol, BigDecimal quantity, SymbolEntity currency, BigDecimal currencyAmount) {
		this.portofolio = requireNonNull(portofolio);
		this.symbol = requireNonNull(symbol);
		this.quantity = requireNonNull(quantity);
		this.currency = requireNonNull(currency);
		this.currencyAmount = requireNonNull(currencyAmount);
	}

	public SymbolEntity getSymbol() {
		return symbol;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public SymbolEntity getCurrency() {
		return currency;
	}

	public BigDecimal getCurrencyAmount() {
		return currencyAmount;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this) //
				.add("symbol", symbol) //
				.add("quantity", quantity) //
				.add("currency", currency) //
				.add("currencyAmount", currencyAmount).toString();
	}

	public void makeDeposit(BigDecimal quantity, BigDecimal currencyAmount) {
		requireNonNull(quantity);
		requireNonNull(currencyAmount);

		Preconditions.checkArgument(quantity.signum() > 0, "quantity must be positive: %s", quantity);
		Preconditions.checkArgument(currencyAmount.signum() > 0, "currencyAmount must be positive: %s", currencyAmount);

		this.quantity = getQuantity().add(quantity);
		this.currencyAmount = getCurrencyAmount().add(currencyAmount);
	}

	public void withdraw(BigDecimal quantity) {
		requireNonNull(quantity);

		Preconditions.checkArgument(getQuantity().compareTo(quantity) >= 0,
				"Too much to withdraw: quantity to withdraw=%s, current quantity=%s", quantity, getQuantity());

		BigDecimal currencyAmountToWithdraw = divide(getCurrencyAmount(), getQuantity()).multiply(quantity);

		this.quantity = getQuantity().subtract(quantity);
		this.currencyAmount = getCurrencyAmount().subtract(currencyAmountToWithdraw);
	}
}