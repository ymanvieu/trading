package fr.ymanvieu.trading.datacollect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import fr.ymanvieu.trading.common.TradingCommonApplication;

@SpringBootApplication(scanBasePackageClasses = {TradingCommonApplication.class, TradingDataCollectApplication.class})
public class TradingDataCollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingDataCollectApplication.class, args);
	}

	@Profile("war-data-collect")
	@Configuration
	static class TradingDataCollectWarApplication extends SpringBootServletInitializer {}
}
