package fr.ymanvieu.trading.common.provider.rate.yahoo;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;

@Component
public class YahooCurrencyProvider extends AbstractYahooProvider implements LatestRateProvider {

	// TODO add in DB
	private final static List<String> CURRENCIES = Arrays.asList("USDEUR=X,USDJPY=X,USDGBP=X,USDAUD=X,USDCHF=X,USDCAD=X,USDMXN=X,USDCNY=X,USDNZD=X,USDSEK=X,USDRUB=X,USDHKD=X,USDNOK=X,USDSGD=X,USDTRY=X,USDKRW=X,USDZAR=X,USDBRL=X,USDINR=X".split(","));

	@Override
	public List<Quote> getLatestRates() {
		return getRates(CURRENCIES);
	}
}
