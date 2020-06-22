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
package fr.ymanvieu.trading.common.provider.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import fr.ymanvieu.trading.common.provider.UpdatedPair;
import fr.ymanvieu.trading.common.provider.entity.PairEntity;

public interface PairRepository extends JpaRepository<PairEntity, Integer>, QuerydslPredicateExecutor<PairEntity> {

	PairEntity findBySymbolAndProviderCode(String symbol, String provider);

	List<PairEntity> findAllByProviderCode(String providerCode);
	
	@Query("SELECT lr.date as lastUpdate, p.symbol as symbol, p.name as name, p.source as source, p.target as target, p.exchange as exchange, p.providerCode as providerCode "
			+ "FROM #{#entityName} p LEFT JOIN LatestRate lr on p.source=lr.fromcur AND p.target=lr.tocur")
	List<UpdatedPair> findAllUpdatedPair();
	
	@Query("SELECT lr.date as lastUpdate, p.symbol as symbol, p.name as name, p.source as source, p.target as target, p.exchange as exchange, p.providerCode as providerCode "
			+ "FROM #{#entityName} p LEFT JOIN LatestRate lr on p.source=lr.fromcur AND p.target=lr.tocur "
			+ "where lower(p.symbol) like concat('%', lower(:symbol), '%') OR lower(p.name) like concat('%', lower(:name), '%')")
	List<UpdatedPair> findAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(String symbol, String name);
}