package fr.ymanvieu.trading.datacollect.rate.scheduler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.common.provider.ProviderType;
import fr.ymanvieu.trading.common.provider.RateProviderService;
import fr.ymanvieu.trading.datacollect.rate.RateUpdaterService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(name = "trading.scheduler.type", havingValue = "fixed-rate")
public class FixedRateSchedulerService {

	@Autowired
	private RateUpdaterService dataUpdater;

	@Autowired
	private RateProviderService dataProvider;

	@Scheduled(fixedRateString = "${scheduler.interval.fixed-rate.forex}")
	public void updateForex() throws IOException {
		updateRates(ProviderType.FOREX);
	}

	@Scheduled(fixedRateString = "${scheduler.interval.fixed-rate.stock}")
	public void updateStock() throws IOException {
		updateRates(ProviderType.STOCK);
	}

	private void updateRates(ProviderType type) throws IOException {
		dataUpdater.updateRates(dataProvider.getLatestProvider(type));
	}

}
