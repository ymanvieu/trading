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

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
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
	private int id;
	
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
