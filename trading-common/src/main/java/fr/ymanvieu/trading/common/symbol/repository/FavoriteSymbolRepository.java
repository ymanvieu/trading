package fr.ymanvieu.trading.common.symbol.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.ymanvieu.trading.common.symbol.entity.FavoriteSymbolEntity;
import fr.ymanvieu.trading.common.symbol.entity.FavoriteSymbolPK;

public interface FavoriteSymbolRepository  extends JpaRepository<FavoriteSymbolEntity, FavoriteSymbolPK> {

	void deleteByFromSymbolCodeAndToSymbolCodeAndUserId(String fromSymbolCode, String toSymbolCode, Integer userId);
}
