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

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.portofolio.Order;
import fr.ymanvieu.trading.portofolio.OrderException;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.user.entity.UserEntity;

@Entity
@Table(name = "portofolio")
public class PortofolioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private UserEntity user;

	@ManyToOne
	@JoinColumn(name = "base_currency_code", nullable = false)
	private SymbolEntity baseCurrency;

	@Column(precision = 20, scale = 10, nullable = false)
	private BigDecimal amount;

	@Version
	private long version;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portofolio")
	private List<AssetEntity> assets;

	protected PortofolioEntity() {
	}

	public PortofolioEntity(UserEntity user, SymbolEntity baseCurrency, BigDecimal amount) {
		this.user = requireNonNull(user, "user is null");
		this.baseCurrency = requireNonNull(baseCurrency, "baseCurrency is null");
		this.amount = requireNonNull(amount, "amount is null");
		this.assets = new ArrayList<>();
	}

	public UserEntity getUser() {
		return user;
	}

	public SymbolEntity getBaseCurrency() {
		return baseCurrency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public List<AssetEntity> getAssets() {
		return Collections.unmodifiableList(assets);
	}

	public AssetEntity getAsset(String symbolCode) {
		return getAssets().stream().filter(a -> a.getSymbol().getCode().equals(symbolCode)).findFirst().orElse(null);
	}

	public SymbolEntity getCurrencyFor(SymbolEntity symbol) {
		return (symbol.getCurrency() != null) ? symbol.getCurrency() : getBaseCurrency();
	}

	public List<SymbolEntity> getCurrencies() {
		List<SymbolEntity> currencies = getAssets().stream() //
				.filter(a -> a.getSymbol().getCurrency() == null) //
				.map(a -> a.getSymbol()) //
				.collect(toList());

		currencies.add(getBaseCurrency());

		return currencies;
	}

	public Order buy(SymbolEntity fromSymbol, float quantity, RateService rateService) throws OrderException {

		Objects.requireNonNull(fromSymbol, "fromSymbol must be not null");

		Preconditions.checkArgument(quantity > 0, "quantity must be positive: %s", getBaseCurrency());
		Preconditions.checkArgument(!getBaseCurrency().equals(fromSymbol), "Cannot buy baseCurrency: %s", getBaseCurrency());

		final SymbolEntity currency = getCurrencyFor(fromSymbol);

		Quote q = rateService.getLatest(fromSymbol.getCode(), currency.getCode());

		final BigDecimal currencyAmount;

		if (currency.equals(getBaseCurrency())) {
			currencyAmount = getAmount();
		} else if (getAsset(currency.getCode()) != null) {
			currencyAmount = getAsset(currency.getCode()).getQuantity();
		} else {
			currencyAmount = ZERO;
		}

		BigDecimal amountNeeded = q.getPrice().multiply(new BigDecimal(quantity));

		if (currencyAmount.compareTo(amountNeeded) < 0) {
			throw OrderException.NOT_ENOUGH_FUND(fromSymbol.getCode(), quantity, currency.getCode(), currencyAmount.floatValue(),
					amountNeeded.floatValue());
		}

		AssetEntity ownedAsset = getAsset(fromSymbol.getCode());

		if (ownedAsset == null) {
			ownedAsset = new AssetEntity(this, fromSymbol, ZERO, currency, ZERO);
			assets.add(ownedAsset);
		}

		BigDecimal fundAfterPurchase = currencyAmount.subtract(amountNeeded);

		if (currency.equals(getBaseCurrency())) {
			this.amount = fundAfterPurchase;
		} else {
			AssetEntity currencyAsset = getAsset(currency.getCode());
			currencyAsset.withdraw(amountNeeded);
		}

		ownedAsset.makeDeposit(new BigDecimal(quantity), amountNeeded);

		return new Order(currency, amountNeeded.floatValue(), fromSymbol, quantity);
	}

	public Order sell(SymbolEntity fromSymbol, float quantity, RateService rateService) throws OrderException {
		Objects.requireNonNull(fromSymbol, "fromSymbol must be not null");

		Preconditions.checkArgument(quantity > 0, "quantity must be positive: %s", getBaseCurrency());
		Preconditions.checkArgument(!getBaseCurrency().equals(fromSymbol), "Cannot sell baseCurrency: %s", getBaseCurrency());

		final AssetEntity ownedAsset = getAsset(fromSymbol.getCode());

		if (ownedAsset == null) {
			throw OrderException.NO_QUANTITY_OWNED(fromSymbol.getCode());
		}

		BigDecimal quantityAfterSell = ownedAsset.getQuantity().subtract(new BigDecimal(quantity));

		if (quantityAfterSell.signum() < 0) {
			throw OrderException.NOT_ENOUGH_OWNED(fromSymbol.getCode(), ownedAsset.getQuantity().floatValue(), quantity);
		}

		final SymbolEntity currency = getCurrencyFor(fromSymbol);

		Quote q = rateService.getLatest(fromSymbol.getCode(), currency.getCode());

		// buy currency
		BigDecimal purchasedValue = q.getPrice().multiply(new BigDecimal(quantity));

		if (currency.equals(getBaseCurrency())) {
			amount = amount.add(purchasedValue);
		} else {

			AssetEntity purchasedAsset = getAsset(currency.getCode());

			if (purchasedAsset == null) {
				purchasedAsset = new AssetEntity(this, currency, ZERO, getBaseCurrency(), ZERO);
				assets.add(purchasedAsset);
			}

			Quote currencyQuote = rateService.getLatest(purchasedAsset.getSymbol().getCode(), purchasedAsset.getCurrency().getCode());
			BigDecimal purchasedValueTotalPrice = currencyQuote.getPrice().multiply(purchasedValue);

			purchasedAsset.makeDeposit(purchasedValue, purchasedValueTotalPrice);

		}

		// sell owned
		if (quantityAfterSell.signum() <= 0) {
			removeAsset(ownedAsset);
		} else {
			ownedAsset.withdraw((new BigDecimal(quantity)));
		}

		return new Order(ownedAsset.getSymbol(), quantity, currency, purchasedValue.floatValue());
	}

	private void removeAsset(AssetEntity ownedAsset) {
		assets.removeIf(a -> a.getSymbol().getCode().equals(ownedAsset.getSymbol().getCode()));
	}
}