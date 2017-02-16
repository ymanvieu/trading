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
package fr.ymanvieu.trading.symbol.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@Transactional(readOnly = true)
public interface SymbolRepository extends JpaRepository<SymbolEntity, String>, QueryDslPredicateExecutor<SymbolEntity> {

	@Override
	List<SymbolEntity> findAll(Predicate predicate, OrderSpecifier<?>... orders);
}