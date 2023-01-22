package fr.ymanvieu.trading.common.provider.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.entity.PairEntity;
import fr.ymanvieu.trading.common.symbol.mapper.SymbolMapper;

@Mapper(config = MapstructConfig.class, uses = SymbolMapper.class)
public interface PairMapper {

	Pair mapToPair(PairEntity entity);
	
	List<Pair> mapToPairs(List<PairEntity> entities);
}
