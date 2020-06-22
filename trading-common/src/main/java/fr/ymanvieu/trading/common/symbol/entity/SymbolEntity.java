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
package fr.ymanvieu.trading.common.symbol.entity;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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