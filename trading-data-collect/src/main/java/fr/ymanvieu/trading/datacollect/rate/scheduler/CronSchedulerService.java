package fr.ymanvieu.trading.datacollect.rate.scheduler;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "trading.scheduler.type", havingValue = "cron")
public class CronSchedulerService extends FixedRateSchedulerService {

	@Scheduled(zone = "UTC", cron = "${scheduler.interval.cron.forex}")
	@Override
	public void updateForex() throws IOException {
		super.updateForex();
	}

	@Scheduled(zone = "UTC", cron = "${scheduler.interval.cron.stock}")
	@Override
	public void updateStock() throws IOException {
		super.updateStock();
	}
}
