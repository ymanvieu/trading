package fr.ymanvieu.trading.datacollect.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "fr.ymanvieu.trading.common")
@EnableJpaRepositories(basePackages = "fr.ymanvieu.trading.common")
public class RepositoryTestConfig {
}
