package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.config.ProviderConfig;
import fr.ymanvieu.trading.common.provider.LookupInfo;

@RestClientTest
@Import({YahooLookup.class, ProviderConfig.class})
public class YahooLookupTest {

	@Value("classpath:provider/lookup/yahoo/search_ubi.json")
	private Resource searchResult;

	@Value("classpath:provider/rate/yahoo/latest_ubi.json")
	private Resource latestUbi;

	@Autowired
	private MockRestServiceServer server;

	@Autowired
	private YahooLookup yahooLookup;

	@Test
	public void testSearch() throws Exception {
		server.expect(anything()).andRespond(withSuccess(searchResult, MediaType.APPLICATION_JSON));

		List<LookupInfo> result = yahooLookup.search("ubi");

		assertThat(result).containsOnlyOnce(new LookupInfo("UBI.PA", "Ubisoft Entertainment SA", "Paris", "Equity", "YAHOO"));
	}

	@Test
	public void testGetDetails() throws Exception {
		server.expect(anything()).andRespond(withSuccess(searchResult, MediaType.APPLICATION_JSON));
		server.expect(anything()).andRespond(withSuccess(latestUbi, MediaType.APPLICATION_JSON));

		assertThat(yahooLookup.getDetails("UBI.PA").getCurrency()).isEqualTo("EUR");
	}

	private static Object[][] testParseSource() {
		return new Object[][] {
				{ "BTCUSD=X", "BTC" },
				{ "XAU=X", "USD" },
				{ "EDF.PA", "EDF" },
				{ "MSFT", "MSFT" },
				{ "TS_B.TO", "TS_B" },
				{ "CL=F", "CL=F" },
				{ "DOGE-USD", "DOGE" },
				{ "THQN-B.ST", "THQN-B" },
				{ "RDS-A", "RDS-A" },
				{ "005930.KS", "005930" },
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testParseSource(String code, String expectedResult) {
		assertThat(YahooLookup.parseSource(code)).isEqualTo(expectedResult);
	}

	private static Object[][] testParseTarget() {
		return new Object[][] {
				{ "BTCUSD=X", "USD" },
				{ "XAU=X", "XAU" },
				{ "EDF.PA", null },
				{ "MSFT", null },
				{ "TS_B.TO", null },
				{ "CL=F", null },
				{ "DOGE-USD", "USD" },
				{ "THQN-B.ST", null },
				{ "RDS-A", null },
				{ "005930.KS", null },
		};
	}

	@ParameterizedTest
	@MethodSource
	public void testParseTarget(String code, String expectedResult) {
		assertThat(YahooLookup.parseTarget(code)).isEqualTo(expectedResult);
	}
}
