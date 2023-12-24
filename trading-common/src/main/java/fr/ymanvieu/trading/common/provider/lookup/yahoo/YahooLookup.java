package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import static fr.ymanvieu.trading.common.provider.rate.yahoo.YahooSymbolParser.parseSource;
import static fr.ymanvieu.trading.common.provider.rate.yahoo.YahooSymbolParser.parseTarget;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;
import fr.ymanvieu.trading.common.provider.lookup.LookupProvider;
import fr.ymanvieu.trading.common.provider.lookup.ProviderCode;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel.Chart.Result;
import fr.ymanvieu.trading.common.provider.rate.yahoo.YahooHistoricalModel.Chart.Result.Meta;

@Component
public class YahooLookup implements LookupProvider {

	@Value("${provider.yahoo.url.lookup}")
	private String url;

	@Value("${provider.yahoo.url.history}")
	private String urlHistory;

	@Autowired
	private RestTemplate rt;

	@Override
	public List<LookupInfo> search(String symbolOrName) throws IOException {
		YahooLookupModel result = rt.getForObject(url, YahooLookupModel.class, symbolOrName);

		if (result == null) {
			return List.of();
		}

		return result.getItems().stream()
		.map(r -> new LookupInfo(r.getSymbol(), r.getName(), r.getExchDisp(), r.getTypeDisp(), getProviderCode()))
		.collect(Collectors.toList());
	}

	@Override
	public LookupDetails getDetails(String code) throws IOException {
		List<LookupInfo> result = search(code);

		LookupInfo cl = result.stream().filter(li -> li.code().equals(code)).findFirst().orElse(null);

		String name = cl.name();
		String source = parseSource(code);
		String currency = parseTarget(code);

		if (currency == null) {
			currency = rt.getForObject(urlHistory, YahooHistoricalModel.class, code, "1d")
				.getChart().getResult().stream()
				.map(Result::getMeta)
				.map(Meta::getCurrency).findFirst().orElseThrow().toUpperCase();
		}

		return new LookupDetails(code, name, source, currency, cl.exchange(), getProviderCode());
	}

	@Override
	public String getProviderCode() {
		return ProviderCode.YAHOO.name();
	}
}
