package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import fr.ymanvieu.trading.common.config.ProviderConfig;
import fr.ymanvieu.trading.common.provider.Quote;

@RestClientTest
@Import({YahooCurrencyProvider.class, ProviderConfig.class})
public class YahooCurrencyProviderTest {

	@Value("classpath:provider/rate/yahoo/latest_currencies.json")
	private Resource lastest;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private YahooCurrencyProvider yahooCurrencyProvider;

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(lastest, MediaType.APPLICATION_JSON));

		Quote expectedUsdEurRate = new Quote(USD, EUR, new BigDecimal("0.8322"), parse("2017-09-11T10:01:00+02:00"));

		List<Quote> result = yahooCurrencyProvider.getRates();

		assertThat(result).containsOnlyOnce(expectedUsdEurRate);
	}
}
