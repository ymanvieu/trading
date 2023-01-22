package fr.ymanvieu.trading.common.symbol.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;

@Mapper(config = MapstructConfig.class)
public interface SymbolMapper {
	
	Symbol mapToSymbol(SymbolEntity entity);
	
	List<Symbol> mapToSymbols(List<SymbolEntity> entity);
}
