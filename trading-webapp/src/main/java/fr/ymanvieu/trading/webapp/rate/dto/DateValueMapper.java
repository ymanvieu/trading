package fr.ymanvieu.trading.webapp.rate.dto;

import java.util.List;

import org.mapstruct.Mapper;

import fr.ymanvieu.trading.common.config.MapstructConfig;
import fr.ymanvieu.trading.common.rate.DateValue;

@Mapper(config = MapstructConfig.class)
public interface DateValueMapper {

	DateValueDTO toDateValueDto(DateValue value);

	List<DateValueDTO> toDateValueDto(List<DateValue> values);
}
