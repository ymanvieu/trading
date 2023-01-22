package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import fr.ymanvieu.trading.common.config.ProviderConfig;
import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.PairService;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.symbol.Symbol;

@RestClientTest
@Import({YahooStockProvider.class, ProviderConfig.class})
public class YahooStockProviderTest {

	@Value("classpath:provider/rate/yahoo/histo_aapl.json")
	private Resource histoAapl;

	@Value("classpath:provider/rate/yahoo/latest.json")
	private Resource latest;

	@Value("classpath:provider/rate/yahoo/latest_ubi.json")
	private Resource lastestUbi;

	@Autowired
	private MockRestServiceServer server;

	@MockBean
	private PairService pairService;

	@Autowired
	private YahooStockProvider yahooStock;

	@Test
	public void testGetHistoricalRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(histoAapl, MediaType.APPLICATION_JSON));
		
		Quote expectedRate0 = new Quote("AAPL", USD, new BigDecimal("0.5133928656578064"), parse("1980-12-12T09:00:00+00:00"));
		Quote expectedRate3 = new Quote("AAPL", USD, new BigDecimal("160.86000061035156"), parse("2017-09-08T22:00:00+02:00"));

		List<Quote> result = yahooStock.getHistoricalRates("AAPL");
		
		assertThat(result).hasSize(443).containsOnlyOnce(expectedRate0, expectedRate3);
	}

	@Test
	public void testGetRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(latest, MediaType.APPLICATION_JSON));
		
		Quote expectedUbisoftRate = new Quote("UBI", EUR, new BigDecimal("57.52"), parse("2017-09-08T17:35:14+02:00"));
		Quote expectedLitecoinRate = new Quote("LTC", USD, new BigDecimal("67.29928"), parse("2017-09-10T15:19:23+02:00"));
		

		var symbols = List.of(
		new Pair().setSymbol("UBI.PA").setName("Ubi").setSource(new Symbol().setCode("UBI")).setTarget(new Symbol().setCode(EUR)).setProviderCode("YAHOO"),
		new Pair().setSymbol("LTCUSD=X").setName("Litecoin").setSource(new Symbol().setCode("LTC")).setTarget(new Symbol().setCode(USD)).setProviderCode("YAHOO")
		);
		
		when(pairService.getAllFromProvider(any())).thenReturn(symbols);

		List<Quote> result = yahooStock.getRates();

		assertThat(result).containsOnlyOnce(expectedLitecoinRate, expectedUbisoftRate);
	}

	@Test
	public void testGetLatestRate() {
		server.expect(anything()).andRespond(withSuccess(lastestUbi, MediaType.APPLICATION_JSON));

		Quote result = yahooStock.getLatestRate("UBI.PA");

		assertThat(result).isEqualTo(new Quote("UBI.PA", null, new BigDecimal("57.52"), parse("2017-09-08T17:35:14+02:00")));
	}
}
