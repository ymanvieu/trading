package fr.ymanvieu.trading.scenario.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ScenarioConfig {

    @Bean
    public MockRestServiceServer mockRestServiceServer(RestTemplate restTemplate) {
        return MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }
}
