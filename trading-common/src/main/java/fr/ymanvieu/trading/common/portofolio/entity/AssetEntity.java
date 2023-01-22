package fr.ymanvieu.trading.common.portofolio.entity;

import static fr.ymanvieu.trading.common.util.MathUtils.divide;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.common.audit.AuditableEntity;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "assets", uniqueConstraints = @UniqueConstraint(columnNames = { "portofolio_id", "symbol_code" }))
@Getter
@ToString
public class AssetEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

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
