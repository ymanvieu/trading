/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.trading.common.rate.entity;

import java.math.BigDecimal;
import java.time.Instant;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
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
