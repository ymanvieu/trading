package fr.ymanvieu.trading.common.portofolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.security.core.Authentication;

import fr.ymanvieu.trading.common.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.symbol.Symbol;

public interface AssetRepository extends JpaRepository<AssetEntity, Integer>, QuerydslPredicateExecutor<AssetEntity> {

	List<AssetEntity> findAllBySymbolCodeAndCurrencyCode(String symbolCode, String currencyCode);

	@Modifying(clearAutomatically = true)
	@Query("""
 		update AssetEntity
 			set 
 				symbol.code=:#{#newSymbol.code}, 
 				currency.code=:#{#newSymbol.currency.code},
				lastModifiedDate=:#{T(java.time.Instant).now()},
				lastModifiedBy=:#{#lastModifiedBy}
 			where symbol.code=:#{#oldPair.source.code} and currency.code=:#{#oldPair.target.code}
 	""")
	int update(Pair oldPair, Symbol newSymbol, String lastModifiedBy);
}
