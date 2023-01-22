package fr.ymanvieu.trading.test.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import fr.ymanvieu.trading.webapp.TradingWebApplication;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
	basePackageClasses = TradingWebApplication.class,
	useDefaultFilters = false, 
	includeFilters = @Filter(type = FilterType.REGEX, pattern = ".*dto\\..*MapperImpl")
)
public class MapperTestConfig {

}
