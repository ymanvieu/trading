package fr.ymanvieu.trading.datacollect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource("classpath:scheduler.properties")
public class SchedulerConfig {
}
