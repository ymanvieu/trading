package fr.ymanvieu.trading.datacollect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@EnableAsync
@PropertySource("classpath:trading-data-collect.properties")
@Configuration
public class TradingDataCollectConfig {

}
