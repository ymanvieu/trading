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
package fr.ymanvieu.trading.rate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import fr.ymanvieu.trading.rate.entity.LatestRate;

public interface LatestRateRepository extends JpaRepository<LatestRate, Long>, QueryDslPredicateExecutor<LatestRate> {

	@Modifying
	@Query("delete FROM #{#entityName} where fromcur.code=:code or tocur.code=:code")
	int deleteByFromcurCodeOrTocurCode(@Param("code") String code);

	LatestRate findByFromcurCodeAndTocurCode(String fromcur, String tocur);

	/** Only for Stocks */
	LatestRate findByFromcurCode(String code);
}