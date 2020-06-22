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
package fr.ymanvieu.trading.common.rate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;

@Transactional(readOnly = true)
public interface LatestRateRepository extends JpaRepository<LatestRate, Integer>, QuerydslPredicateExecutor<LatestRate> {

	@Transactional
	@Modifying
	@Query("delete FROM #{#entityName} where fromcur.code=:code or tocur.code=:code")
	int deleteByFromcurCodeOrTocurCode(@Param("code") String code);

	LatestRate findByFromcurCodeAndTocurCode(String fromcur, String tocur);

	@Query("SELECT (fs.username is not null) as favorite, lr.fromcur as fromcur, lr.tocur as tocur, lr.value as value, lr.date as date FROM #{#entityName} lr LEFT JOIN FavoriteSymbolEntity fs ON lr.fromcur=fs.fromSymbolCode AND lr.tocur=fs.toSymbolCode AND fs.username=:username")
	List<FavoriteRate> findAllWithFavorites(@Param("username") String username);
}