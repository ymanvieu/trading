package fr.ymanvieu.trading.common.symbol.entity;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "symbols")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@EqualsAndHashCode(of = "code")
@EntityListeners(AuditingEntityListener.class)
public class SymbolEntity {

	@Id
	@Nonnull
	@Column(length = 8)
	private String code;

	private String name;

	@Column(name = "country_flag", length = 16)
	private String countryFlag;

	@ManyToOne
	@JoinColumn(name = "currency", referencedColumnName = "code")
	private SymbolEntity currency;
	
	@CreatedBy
	@Column(name = "created_by")
	private String createdBy;
	
	@CreatedDate
	@Column(name = "created_date")
	private Instant createdDate;

	public SymbolEntity(String code, String name, String countryFlag, SymbolEntity currency) {
		this.code = code;
		this.name = name;
		this.countryFlag = countryFlag;
		this.currency = currency;
	}
}
