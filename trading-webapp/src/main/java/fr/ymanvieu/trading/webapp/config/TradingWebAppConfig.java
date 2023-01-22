package fr.ymanvieu.trading.webapp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@PropertySource({"classpath:trading-webapp.properties", "classpath:oauth2-provider.properties"})
@Configuration
@EnableConfigurationProperties({ RecaptchaProperties.class, JwtProperties.class })
public class TradingWebAppConfig {
}
