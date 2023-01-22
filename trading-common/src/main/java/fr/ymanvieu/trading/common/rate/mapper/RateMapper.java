package fr.ymanvieu.trading.common.rate.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.entity.RateEntity;
import fr.ymanvieu.trading.common.symbol.mapper.SymbolMapper;

@Mapper(config = MapstructConfig.class, uses = SymbolMapper.class)
public interface RateMapper {
	
	Rate mapToRate(RateEntity entity);
	
	List<Rate> mapToRates(List<? extends RateEntity> entites);
}
