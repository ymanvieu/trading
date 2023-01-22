package fr.ymanvieu.trading.common.symbol.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;

public interface SymbolRepository extends JpaRepository<SymbolEntity, String>, QuerydslPredicateExecutor<SymbolEntity> {

	@Override
	List<SymbolEntity> findAll(Predicate predicate, OrderSpecifier<?>... orders);
	
	Optional<SymbolEntity> findOneByCodeAndCurrencyIsNull(String code);
}
