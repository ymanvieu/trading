package fr.ymanvieu.trading.common.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooCurrencyProvider;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooStockProvider;

@Service
public class RateProviderService {

	@Autowired
	private YahooCurrencyProvider currencyProvider;

	@Autowired
	private YahooStockProvider stockProvider;

	public LatestRateProvider getLatestProvider(ProviderType type) {
		LatestRateProvider provider = null;

		switch (type) {
		case FOREX:
			provider = currencyProvider;
			break;
		case STOCK:
			provider = stockProvider;
			break;
		default:
			break;
		}

		return provider;
	}

	public HistoricalRateProvider getHistoricalProvider(ProviderType type) {
		HistoricalRateProvider provider = null;

		switch (type) {
		case STOCK:
			provider = stockProvider;
			break;
		default:
			break;
		}

		return provider;
	}
}
