package fr.ymanvieu.trading.common.rate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;
import fr.ymanvieu.trading.common.rate.entity.LatestRatePK;
import fr.ymanvieu.trading.common.symbol.Symbol;

public interface LatestRateRepository extends JpaRepository<LatestRate, LatestRatePK>, QuerydslPredicateExecutor<LatestRate> {

	@Modifying
	@Query("delete from LatestRate where fromcur.code=:#{#fromcurCode} AND tocur.code=:#{#tocurCode}")
	int deleteByFromcurCodeAndTocurCode(String fromcurCode, String tocurCode);

	LatestRate findByFromcurCodeAndTocurCode(String fromcur, String tocur);

	@Query("SELECT (fs.userId is not null) as favorite, lr.fromcur as fromcur, lr.tocur as tocur, lr.value as value, lr.date as date FROM LatestRate lr LEFT JOIN FavoriteSymbolEntity fs ON lr.fromcur.code=fs.fromSymbolCode AND lr.tocur.code=fs.toSymbolCode AND fs.userId=:userId")
	List<FavoriteRate> findAllWithFavorites(@Param("userId") Integer userId);

	@Modifying(clearAutomatically = true)
	@Query("""
 		update LatestRate 
 			set 
 				fromcur.code=:#{#newSymbol.code}, 
 				tocur.code=:#{#newSymbol.currency.code},
				lastModifiedDate=:#{T(java.time.Instant).now()}
 			where fromcur.code=:#{#oldPair.source.code} and tocur.code=:#{#oldPair.target.code}
 	""")
	int update(Pair oldPair, Symbol newSymbol);
}
