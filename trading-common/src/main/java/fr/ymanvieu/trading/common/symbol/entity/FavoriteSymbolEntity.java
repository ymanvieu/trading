/**
 * Copyright (C) 2019 Yoann Manvieu
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorite_symbol")
@Data
@EqualsAndHashCode(of = {"username", "fromSymbolCode", "toSymbolCode"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(FavoriteSymbolPK.class)
public class FavoriteSymbolEntity implements Serializable {

	private static final long serialVersionUID = 3912360548486565703L;

	@Id
	@Column(name = "from_symbol_code", length = 8)
	private String fromSymbolCode;
	
	@Id
	@Column(name = "to_symbol_code", length = 8)
	private String toSymbolCode;

	@Id
	@Column(length = 50)
	private String username;
}