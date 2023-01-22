package fr.ymanvieu.trading.common.portofolio.entity;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.common.audit.AuditableEntity;
import fr.ymanvieu.trading.common.portofolio.Order;
import fr.ymanvieu.trading.common.portofolio.OrderException;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.user.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "portofolio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class PortofolioEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Nonnull
	@OneToOne
	@JoinColumn(name = "user_id", unique = true, nullable = false)
	private UserEntity user;

	@Getter
	@Nonnull
	@ManyToOne
	@JoinColumn(name = "base_currency_code", nullable = false)
	private SymbolEntity baseCurrency;

	@Getter
	@Nonnull
	@Column(precision = 20, scale = 10, nullable = false)
	private BigDecimal amount;

	@Version
	private long version;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portofolio")
	private List<AssetEntity> assets = new ArrayList<>();

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

	public Order buy(SymbolEntity fromSymbol, double quantity, RateService rateService) throws OrderException {

		Objects.requireNonNull(fromSymbol, "fromSymbol must be not null");

		Preconditions.checkArgument(quantity > 0, "quantity must be positive: %s", getBaseCurrency());
		Preconditions.checkArgument(!getBaseCurrency().equals(fromSymbol), "Cannot buy baseCurrency: %s", getBaseCurrency());

		final SymbolEntity currency = getCurrencyFor(fromSymbol);

		Rate q = rateService.getLatest(fromSymbol.getCode(), currency.getCode());

		final BigDecimal currencyAmount;

		if (currency.equals(getBaseCurrency())) {
			currencyAmount = getAmount();
		} else if (getAsset(currency.getCode()) != null) {
			currencyAmount = getAsset(currency.getCode()).getQuantity();
		} else {
			currencyAmount = ZERO;
		}

		BigDecimal amountNeeded = q.getValue().multiply(BigDecimal.valueOf(quantity));

		if (currencyAmount.compareTo(amountNeeded) < 0) {
			throw OrderException.NOT_ENOUGH_FUND(fromSymbol.getCode(), quantity, currency.getCode(), currencyAmount.doubleValue(),
					amountNeeded.doubleValue());
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

		ownedAsset.makeDeposit(BigDecimal.valueOf(quantity), amountNeeded);

		return new Order(currency, amountNeeded.doubleValue(), fromSymbol, quantity);
	}

	public Order sell(SymbolEntity fromSymbol, double quantity, RateService rateService) throws OrderException {
		Objects.requireNonNull(fromSymbol, "fromSymbol must be not null");

		Preconditions.checkArgument(quantity > 0, "quantity must be positive: %s", getBaseCurrency());
		Preconditions.checkArgument(!getBaseCurrency().equals(fromSymbol), "Cannot sell baseCurrency: %s", getBaseCurrency());

		final AssetEntity ownedAsset = getAsset(fromSymbol.getCode());

		if (ownedAsset == null) {
			throw OrderException.NO_QUANTITY_OWNED(fromSymbol.getCode());
		}

		BigDecimal quantityAfterSell = ownedAsset.getQuantity().subtract(BigDecimal.valueOf(quantity));

		if (quantityAfterSell.signum() < 0) {
			throw OrderException.NOT_ENOUGH_OWNED(fromSymbol.getCode(), ownedAsset.getQuantity().doubleValue(), quantity);
		}

		final SymbolEntity currency = getCurrencyFor(fromSymbol);

		Rate q = rateService.getLatest(fromSymbol.getCode(), currency.getCode());

		// buy currency
		BigDecimal purchasedValue = q.getValue().multiply(BigDecimal.valueOf(quantity));

		if (currency.equals(getBaseCurrency())) {
			amount = amount.add(purchasedValue);
		} else {

			AssetEntity purchasedAsset = getAsset(currency.getCode());

			if (purchasedAsset == null) {
				purchasedAsset = new AssetEntity(this, currency, ZERO, getBaseCurrency(), ZERO);
				assets.add(purchasedAsset);
			}

			Rate currencyQuote = rateService.getLatest(purchasedAsset.getSymbol().getCode(), purchasedAsset.getCurrency().getCode());
			BigDecimal purchasedValueTotalPrice = currencyQuote.getValue().multiply(purchasedValue);

			purchasedAsset.makeDeposit(purchasedValue, purchasedValueTotalPrice);

		}

		// sell owned
		if (quantityAfterSell.signum() <= 0) {
			removeAsset(ownedAsset);
		} else {
			ownedAsset.withdraw((BigDecimal.valueOf(quantity)));
		}

		return new Order(ownedAsset.getSymbol(), quantity, currency, purchasedValue.doubleValue());
	}

	private void removeAsset(AssetEntity ownedAsset) {
		assets.removeIf(a -> a.getSymbol().getCode().equals(ownedAsset.getSymbol().getCode()));
	}
}
