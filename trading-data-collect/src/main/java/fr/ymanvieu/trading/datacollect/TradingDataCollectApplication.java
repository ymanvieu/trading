package fr.ymanvieu.trading.datacollect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.ymanvieu.trading.common.TradingCommonApplication;

@SpringBootApplication(scanBasePackageClasses = {TradingCommonApplication.class, TradingDataCollectApplication.class})
public class TradingDataCollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingDataCollectApplication.class, args);
	}
}
