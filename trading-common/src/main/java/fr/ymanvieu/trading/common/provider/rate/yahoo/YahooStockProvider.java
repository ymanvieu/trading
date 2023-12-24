package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.PairService;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.lookup.ProviderCode;
import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class YahooStockProvider extends AbstractYahooProvider implements LatestRateProvider, HistoricalRateProvider {

	@Autowired
	private PairService pairService;

	@Override
	public List<Quote> getLatestRates() {
		List<Pair> pairs = pairService.getAllFromProvider(ProviderCode.YAHOO.name());

		return getRates(pairs.stream().map(Pair::getSymbol).toList());
	}

	@Override
	public List<Quote> getHistoricalRates() {
		List<Quote> quotes = new ArrayList<>();

		List<Pair> pairs = pairService.getAllFromProvider(ProviderCode.YAHOO.name());

		for (Pair pair : pairs) {
			quotes.addAll(getHistoricalRates(pair.getSymbol()));
		}

		return quotes;
	}
}
