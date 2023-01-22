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

	@Query("""
   		SELECT 
		new fr.ymanvieu.trading.common.provider.UpdatedPair(
			p.id, 
			lr.date, 
			p.symbol, 
			p.name,
			p.source.code,
			p.target.code,
			p.exchange, 
			p.providerCode)
   		FROM #{#entityName} p 
   		LEFT JOIN LatestRate lr on p.source=lr.fromcur AND p.target=lr.tocur
   		""")
	List<UpdatedPair> findAllUpdatedPair();

	@Query("""
   		SELECT 
   		new fr.ymanvieu.trading.common.provider.UpdatedPair(
			p.id, 
			lr.date, 
			p.symbol, 
			p.name, 
			p.source.code,
			p.target.code,
			p.exchange, 
			p.providerCode)
		FROM PairEntity p 
		LEFT JOIN LatestRate lr on p.source=lr.fromcur AND p.target=lr.tocur 
		where lower(p.symbol) like concat('%', lower(:symbol), '%') OR lower(p.name) like concat('%', lower(:name), '%')
		""")
	List<UpdatedPair> findAllUpdatedPairBySymbolContainsIgnoreCaseOrNameContainsIgnoreCase(String symbol, String name);

	boolean existsBySourceCode(String sourceCode);
}
