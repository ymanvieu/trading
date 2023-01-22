package fr.ymanvieu.trading.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.ymanvieu.trading.webapp.controller.ExceptionRestHandler;

@Configuration
@Import({ExceptionRestHandler.class})
public class RestTestConfig {

}
