package fr.ymanvieu.trading.common.provider.rate;

import java.util.List;

import fr.ymanvieu.trading.common.provider.Quote;

public interface LatestRateProvider {

	List<Quote> getLatestRates();

	Quote getLatestRate(String code);
}
