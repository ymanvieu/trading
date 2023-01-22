package fr.ymanvieu.trading.datacollect.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
	basePackages = "fr.ymanvieu.trading.common",
	useDefaultFilters = false, 
	includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*MapperImpl")
)
public class MapperTestConfig {

}
