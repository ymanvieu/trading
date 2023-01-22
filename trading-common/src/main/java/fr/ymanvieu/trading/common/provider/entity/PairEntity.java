package fr.ymanvieu.trading.common.provider.entity;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "pair", uniqueConstraints = @UniqueConstraint(columnNames = { "symbol", "provider_code" }))
@EntityListeners(AuditingEntityListener.class)
public class PairEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length = 16)
	private String symbol;

	@Column(nullable = false, length = 255)
	private String name;

	@ManyToOne
	@JoinColumn(name = "source", nullable = false)
	private SymbolEntity source;

	@ManyToOne
	@JoinColumn(name = "target", nullable = false)
	private SymbolEntity target;
	
	@Column(name = "exchange")
	private String exchange;

	@Column(name = "provider_code", length = 8, nullable = false)
	private String providerCode;
	
	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;

	@CreatedDate
	@Column(name = "created_date")
	private Instant createdDate;

	public PairEntity(String symbol, String name, SymbolEntity source, SymbolEntity target, String exchange, String providerCode) {
		this.symbol = requireNonNull(symbol, "symbol is null");
		this.name = requireNonNull(name, "name is null");
		this.source = source;
		this.target = target;
		this.exchange = exchange;
		this.providerCode = requireNonNull(providerCode, "providerCode is null");
	}
}
