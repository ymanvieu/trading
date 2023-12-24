package fr.ymanvieu.trading.common.provider.lookup.yahoo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
import fr.ymanvieu.trading.common.provider.LookupInfo;

@RestClientTest
@Import({YahooLookup.class, ProviderConfig.class})
public class YahooLookupTest {

	@Value("classpath:provider/lookup/yahoo/search_ubi.json")
	private Resource searchResult;

	@Value("classpath:provider/rate/yahoo/histo_ubi.json")
	private Resource histoUbi;

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
		server.expect(anything()).andRespond(withSuccess(histoUbi, MediaType.APPLICATION_JSON));

		assertThat(yahooLookup.getDetails("UBI.PA").getCurrency()).isEqualTo("EUR");
	}
}
