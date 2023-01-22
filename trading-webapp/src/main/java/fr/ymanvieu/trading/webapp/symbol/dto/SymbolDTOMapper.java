package fr.ymanvieu.trading.webapp.symbol.dto;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.symbol.Symbol;

@Mapper(config = MapstructConfig.class)
public interface SymbolDTOMapper {

	SymbolDTO toDto(Symbol symbol);

	List<SymbolDTO> toDto(List<Symbol> symbols);
	
	SymbolDTO toDto(FavoriteRate.Symbol symbol);
}
