package fr.ymanvieu.trading.common.rate.entity;

import java.math.BigDecimal;
import java.time.Instant;

import javax.annotation.Nonnull;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rates")
@IdClass(HistoricalRatePK.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HistoricalRate extends RateEntity {
	
	public HistoricalRate(SymbolEntity fromcur, SymbolEntity tocur, BigDecimal value, Instant date) {
		super(fromcur, tocur, value, date);
	}
	
	public HistoricalRate(String fromcur, String tocur, BigDecimal value, Instant date) {
		this(new SymbolEntity(fromcur), new SymbolEntity(tocur), value, date);
	}

	@Id
	@Nonnull
	@ManyToOne
	@JoinColumn(name = "fromcur", referencedColumnName = "code", nullable = false)
	public SymbolEntity getFromcur() {
		return fromcur;
	}

	@Id
	@Nonnull
	@ManyToOne
	@JoinColumn(name = "tocur", referencedColumnName = "code", nullable = false)
	public SymbolEntity getTocur() {
		return tocur;
	}

	@Id
	@Nonnull
	@Column(precision = 20, scale = 10, nullable = false)
	public BigDecimal getValue() {
		return value;
	}

	@Nonnull
	@Column(nullable = false)
	public Instant getDate() {
		return date;
	}
}
