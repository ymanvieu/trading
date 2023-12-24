package fr.ymanvieu.trading.common.rate.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.rate.DateValue;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRatePK;
import fr.ymanvieu.trading.common.symbol.Symbol;

public interface HistoricalRateRepository extends HistoricalRateRepositoryCustom, JpaRepository<HistoricalRate, HistoricalRatePK>, QuerydslPredicateExecutor<HistoricalRate> {
	
	@Query("select date as date,value as value from HistoricalRate where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) order by date")
	List<DateValue> findDateValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by extract(YEAR FROM date), extract(month FROM date), extract(day FROM date), extract(HOUR FROM date) order by min(date)")
	List<DateValue> findHourlyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by extract(YEAR FROM date), extract(month FROM date), extract(day FROM date) order by min(date)")
	List<DateValue> findDailyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	@Query("SELECT min(date) as date,avg(value) as value FROM #{#entityName} where fromcur.code=:fromcur and tocur.code=:tocur and (date between :start and :end) group by extract(YEAR FROM date), extract(WEEK FROM date) order by min(date)")
	List<DateValue> findWeeklyValues(@Param("fromcur") String fromcur, @Param("tocur") String tocur, @Param("start") Instant startDate, @Param("end") Instant endDate);

	HistoricalRate findFirstByFromcurCodeAndTocurCode(String fromcur, String tocur, Sort order);

	@Modifying
	@Query("delete from HistoricalRate where fromcur.code=:#{#fromcurCode} AND tocur.code=:#{#tocurCode}")
	void deleteAllByFromcurCodeAndTocurCode(String fromcurCode, String tocurCode);

	@Modifying(clearAutomatically = true)
	@Query("update HistoricalRate set fromcur.code=:#{#newSymbol.code}, tocur.code=:#{#newSymbol.currency.code} where fromcur.code=:#{#oldPair.source.code} and tocur.code=:#{#oldPair.target.code}")
	int updateAll(Pair oldPair, Symbol newSymbol);
}
