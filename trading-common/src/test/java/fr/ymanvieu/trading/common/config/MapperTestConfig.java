package fr.ymanvieu.trading.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import fr.ymanvieu.trading.common.TradingCommonApplication;

@Configuration
@ComponentScan(
	basePackageClasses = TradingCommonApplication.class,
	useDefaultFilters = false, 
	includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*MapperImpl")
)
public class MapperTestConfig {

}
