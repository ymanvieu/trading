package fr.ymanvieu.trading.common.rate.entity;

import java.math.BigDecimal;
import java.time.Instant;

import javax.annotation.Nonnull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "latestrates")
@EntityListeners(AuditingEntityListener.class)
@IdClass(LatestRatePK.class)
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LatestRate extends RateEntity {
	
    private Instant createdDate;
    private Instant lastModifiedDate;
   
	public LatestRate(SymbolEntity fromcur, SymbolEntity tocur, BigDecimal value, Instant date) {
		super(fromcur, tocur, value, date);
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
	
    @CreatedDate 
    @Column(name = "created_date")
	public Instant getCreatedDate() {
		return createdDate;
	}
	
    @LastModifiedDate
    @Column(name = "last_modified_date")
	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}	
}
