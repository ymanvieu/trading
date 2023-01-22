package fr.ymanvieu.trading.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import fr.ymanvieu.trading.common.TradingCommonApplication;

@SpringBootApplication(
	exclude = ErrorMvcAutoConfiguration.class, 
	scanBasePackageClasses = {TradingCommonApplication.class, TradingWebApplication.class},
	scanBasePackages = {"fr.ymanvieu.trading.datacollect.config", "fr.ymanvieu.trading.datacollect.rate"} // for embedded datacollect (prod-package)
)
public class TradingWebApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TradingWebApplication.class, args);
	}
}
