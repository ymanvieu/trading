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

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.rate.DateValue;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRate;

@Transactional(readOnly = true)
public interface HistoricalRateRepository extends HistoricalRateRepositoryCustom, JpaRepository<HistoricalRate, Integer>, QuerydslPredicateExecutor<HistoricalRate> {
	
	@Query("select date as date,value as value from #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) order by date")
	List<DateValue> findDateValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by YEAR(date),DAYOFYEAR(date),HOUR(date) order by min(date)")
	List<DateValue> findHourlyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by YEAR(date),DAYOFYEAR(date) order by min(date)")
	List<DateValue> findDailyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by YEAR(date),WEEK(date) order by min(date)")
	List<DateValue> findWeeklyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	HistoricalRate findFirstByFromcurCodeAndTocurCode(String fromcur, String tocur, Sort order);
}