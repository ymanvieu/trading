package fr.ymanvieu.trading.common.provider.rate.yahoo;

import static fr.ymanvieu.trading.common.symbol.Currency.EUR;
import static fr.ymanvieu.trading.common.symbol.Currency.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
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

	@Value("classpath:provider/rate/yahoo/histo_ubi.json")
	private Resource histoUbi;

	@Value("classpath:provider/rate/yahoo/histo_not_found.json")
	private Resource histoNotFound;

	@Autowired
	private MockRestServiceServer server;

	@MockBean
	private PairService pairService;

	@Autowired
	private YahooStockProvider yahooStockProvider;

	@Test
	public void getHistoricalRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(histoAapl, MediaType.APPLICATION_JSON));
		
		Quote expectedRate0 = new Quote("AAPL", USD, new BigDecimal("0.13002200424671173"), parse("1980-12-12T14:30:00+00:00"));
		Quote expectedRate3 = new Quote("AAPL", USD, new BigDecimal("172.41000366210938"), parse("2023-05-25T20:00:04+00:00"));

		List<Quote> result = yahooStockProvider.getHistoricalRates("AAPL");
		
		assertThat(result).hasSize(155).containsOnlyOnce(expectedRate0, expectedRate3);
	}

	@Test
	public void getLatestRates() throws Exception {
		server.expect(anything()).andRespond(withSuccess(histoUbi, MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(histoAapl, MediaType.APPLICATION_JSON));

		Quote expectedUbisoftRate = new Quote("UBI", EUR, new BigDecimal("24.21"), parse("2023-05-26T09:40:28+02:00"));
		Quote expectedAppleRate = new Quote("AAPL", USD, new BigDecimal("172.99"), parse("2023-05-25T22:00:04+02:00"));
		

		var symbols = List.of(
		new Pair().setSymbol("UBI.PA").setName("Ubi").setSource(new Symbol().setCode("UBI")).setTarget(new Symbol().setCode(EUR)).setProviderCode("YAHOO"),
		new Pair().setSymbol("AAPL").setName("Apple").setSource(new Symbol().setCode("APPL")).setTarget(new Symbol().setCode(USD)).setProviderCode("YAHOO")
		);
		
		when(pairService.getAllFromProvider(any())).thenReturn(symbols);

		List<Quote> result = yahooStockProvider.getLatestRates();

		assertThat(result).containsOnlyOnce(expectedAppleRate, expectedUbisoftRate);
	}

	@Test
	public void getLatestRates_ubiNotFound() throws Exception {
		server.expect(anything()).andRespond(withResourceNotFound().body(histoNotFound).contentType(MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(histoAapl, MediaType.APPLICATION_JSON));

		Quote expectedAppleRate = new Quote("AAPL", USD, new BigDecimal("172.99"), parse("2023-05-25T22:00:04+02:00"));


		var symbols = List.of(
		new Pair().setSymbol("UBI.PA").setName("Ubi").setSource(new Symbol().setCode("UBI")).setTarget(new Symbol().setCode(EUR)).setProviderCode("YAHOO"),
		new Pair().setSymbol("AAPL").setName("Apple").setSource(new Symbol().setCode("APPL")).setTarget(new Symbol().setCode(USD)).setProviderCode("YAHOO")
		);

		when(pairService.getAllFromProvider(any())).thenReturn(symbols);

		List<Quote> result = yahooStockProvider.getLatestRates();

		assertThat(result).containsExactlyInAnyOrder(expectedAppleRate);
	}

	@Test
	public void getLatestRate() {
		server.expect(anything()).andRespond(withSuccess(histoUbi, MediaType.APPLICATION_JSON));

		Quote result = yahooStockProvider.getLatestRate("UBI.PA");

		assertThat(result).isEqualTo(new Quote("UBI", EUR, new BigDecimal("24.21"), parse("2023-05-26T09:40:28+02:00")));
	}
}
