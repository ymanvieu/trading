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
package fr.ymanvieu.forex.core.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;

public interface SymbolRepository extends JpaRepository<SymbolEntity, String>, QueryDslPredicateExecutor<SymbolEntity> {

	SymbolEntity findByCode(String code);

	List<SymbolEntity> findAllByCurrencyNotNullOrderByCode();

	@Modifying
	int deleteByCode(String code);

	List<SymbolEntity> findAllByCurrencyCode(String code);
}