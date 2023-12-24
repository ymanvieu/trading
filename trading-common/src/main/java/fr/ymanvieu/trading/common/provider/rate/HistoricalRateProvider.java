package fr.ymanvieu.trading.common.provider.rate;

import java.util.List;

import fr.ymanvieu.trading.common.provider.Quote;

public interface HistoricalRateProvider {

	List<Quote> getHistoricalRates();
	
	List<Quote> getHistoricalRates(String code);
	
}
