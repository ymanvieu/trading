package fr.ymanvieu.trading.webapp.rate.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.webapp.symbol.dto.SymbolDTOMapper;

@Mapper(config = MapstructConfig.class, uses = SymbolDTOMapper.class)
public interface RateDTOMapper {

	@Mapping(target = "favorite", ignore = true)
	RateDTO toRateDto(Rate value);
	List<RateDTO> toRateDto(List<Rate> values);
	
	@Mapping(target = "favorite", ignore = true)
	@Mapping(source = "code", target = "fromcur.code")
	@Mapping(source = "currency", target = "tocur.code")
	@Mapping(source = "price", target = "value")
	@Mapping(source = "time", target = "date")
	RateDTO toRateDto(Quote rate);
	
	RateDTO favoriteRateToRateDto(FavoriteRate value);
	List<RateDTO> favoriteRatesToRateDtos(List<FavoriteRate> values);
}
