package fr.ymanvieu.trading.common.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("classpath:provider.properties")
public class ProviderConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder rtb) {
        return rtb.build();
    }
}
