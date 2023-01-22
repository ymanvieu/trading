package fr.ymanvieu.trading.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:recaptcha.properties")
@Configuration
public class RecaptchaConfig {

}
