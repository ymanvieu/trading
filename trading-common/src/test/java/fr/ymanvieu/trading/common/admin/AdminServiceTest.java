package fr.ymanvieu.trading.common.admin;

import static fr.ymanvieu.trading.common.rate.entity.QHistoricalRate.historicalRate;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.common.portofolio.PortofolioService;
import fr.ymanvieu.trading.common.provider.LookupDetails;
import fr.ymanvieu.trading.common.provider.LookupInfo;
import fr.ymanvieu.trading.common.provider.LookupService;
import fr.ymanvieu.trading.common.provider.Pair;
import fr.ymanvieu.trading.common.provider.PairException;
import fr.ymanvieu.trading.common.provider.PairService;
import fr.ymanvieu.trading.common.provider.ProviderType;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.provider.RateProviderService;
import fr.ymanvieu.trading.common.provider.mapper.PairMapperImpl;
import fr.ymanvieu.trading.common.provider.rate.HistoricalRateProvider;
import fr.ymanvieu.trading.common.provider.rate.LatestRateProvider;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.common.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.common.symbol.SymbolService;

@DataJpaTest
@ComponentScan(basePackageClasses = {AdminService.class, SymbolService.class, RateService.class, PortofolioService.class})
@Import({PairService.class, PairMapperImpl.class})
public class AdminServiceTest {

	@Autowired
	private PairService pairService;

	@Autowired
	private RateService rateService;

	@MockBean
	private LookupService lookupService;

	@MockBean
	private RateProviderService rateProviderService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private HistoricalRateRepository hRateRepo;

	@Mock
	private HistoricalRateProvider hrp;
	
	@Mock
	private LatestRateProvider lrp;
	
	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testAdd() throws Exception {
		// given
		String symbol = "UBI.PA";
		String name = "Ubi";
		String provider = "Provider";

		when(lookupService.search(provider)).thenReturn(List.of(new LookupInfo(symbol, name, "Paris", "Titres", provider)));
		when(lookupService.getDetails(symbol, provider)).thenReturn(new LookupDetails(symbol, name, "UBI", "EUR", "EPA", provider));

		Quote histoQuote = new Quote(symbol, new BigDecimal("25.2"), parse("2016-06-30T02:00:00+02:00"));
		Quote latestQuote = new Quote(symbol, new BigDecimal("25"), parse("2016-06-30T19:32:00+02:00"));
		
		when(rateProviderService.getHistoricalProvider(ProviderType.STOCK)).thenReturn(hrp);
		when(rateProviderService.getLatestProvider(ProviderType.STOCK)).thenReturn(lrp);
		
		when(hrp.getHistoricalRates(symbol)).thenReturn(List.of(histoQuote));
		when(lrp.getLatestRate(symbol)).thenReturn(latestQuote);

		// when
		PairInfo result = adminService.add(symbol, provider);

		// then
		Pair pairResult = pairService.getForCodeAndProvider(symbol, provider);

		assertThat(result.getCode()).isEqualTo(pairResult.getSymbol());
		assertThat(result.getName()).isEqualTo(pairResult.getName());
		assertThat(result.getQuote().getCode()).isEqualTo(pairResult.getSource().getCode());
		assertThat(result.getQuote().getCurrency()).isEqualTo(pairResult.getTarget().getCode());

		assertThat(result.getQuote().getPrice()).isEqualTo("25");
		assertThat(result.getQuote().getTime()).isEqualTo(parse("2016-06-30T19:32:00+02:00"));

		BooleanExpression exp = historicalRate.fromcur.code.eq("UBI").and(historicalRate.tocur.code.eq("EUR"));

		assertThat(hRateRepo.count(exp)).isEqualTo(2);
		assertThat(rateService.getLatest("UBI", "EUR")).isNotNull();
	}
	
	@Sql("/sql/insert_data_histo.sql")
	@Test
	public void testAdd_LastestRateInHistoList() throws Exception {
		// given
		String symbol = "UBI.PA";
		String name = "Ubi";
		String provider = "Provider";

		when(lookupService.search(provider)).thenReturn(List.of(new LookupInfo(symbol, name, "Paris", "Titres", provider)));
		when(lookupService.getDetails(symbol, provider)).thenReturn(new LookupDetails(symbol, name, "UBI", "EUR", "EPA", provider));

		Quote histoQuote = new Quote(symbol, new BigDecimal("25.2"), parse("2016-06-30T02:00:00+02:00"));
		Quote latestHistoQuote = new Quote(symbol, new BigDecimal("25"), parse("2016-06-30T19:32:00+02:00"));
		Quote latestQuote = new Quote(symbol, new BigDecimal("25.5"), parse("2016-06-30T19:32:00+02:00"));
		
		when(rateProviderService.getHistoricalProvider(ProviderType.STOCK)).thenReturn(hrp);
		when(rateProviderService.getLatestProvider(ProviderType.STOCK)).thenReturn(lrp);
		
		when(hrp.getHistoricalRates(symbol)).thenReturn(asList(histoQuote, latestHistoQuote));
		when(lrp.getLatestRate(symbol)).thenReturn(latestQuote);

		// when
		PairInfo result = adminService.add(symbol, provider);

		// then
		Pair pairResult = pairService.getForCodeAndProvider(symbol, provider);

		assertThat(result.getCode()).isEqualTo(pairResult.getSymbol());
		assertThat(result.getName()).isEqualTo(pairResult.getName());
		assertThat(result.getQuote().getCode()).isEqualTo(pairResult.getSource().getCode());
		assertThat(result.getQuote().getCurrency()).isEqualTo(pairResult.getTarget().getCode());

		assertThat(result.getQuote().getPrice()).isEqualTo("25.5");
		assertThat(result.getQuote().getTime()).isEqualTo(parse("2016-06-30T19:32:00+02:00"));

		Iterable<HistoricalRate> res = hRateRepo.findAll(historicalRate.fromcur.code.eq("UBI").and(historicalRate.tocur.code.eq("EUR")));

		assertThat(res).containsExactly(
				new HistoricalRate("UBI", "EUR", new BigDecimal("25.2"), parse("2016-06-30T02:00:00+02:00")),
				new HistoricalRate("UBI", "EUR", new BigDecimal("25.5"), parse("2016-06-30T19:32:00+02:00")));
		
		
		assertThat(rateService.getLatest("UBI", "EUR")).isNotNull();
	}
	
	@Sql("/sql/insert_data.sql")
	@Test
	public void testAdd_pairAlreadyExists() throws Exception {
		// given
		String symbol = "UBI.PA";
		String provider = "YAHOO";

		// when
		assertThatThrownBy(() -> adminService.add(symbol, provider))
				.isInstanceOf(PairException.class)
				.hasMessage("pair.error.already_exists: [UBI.PA, YAHOO]");
	}

	@Test
	public void testAdd_currencyNotExist() throws Exception {
		// given
		String symbol = "UBI.PA";
		String name = "Ubi";
		String provider = "Provider";

		when(lookupService.getDetails(symbol, provider)).thenReturn(new LookupDetails(symbol, name, "UBI", "XXX", "EPA", provider));

		// when
		assertThatThrownBy(() -> adminService.add(symbol, provider))
			.isInstanceOf(PairException.class)
			.hasMessage("pair.error.currency-not-found: [XXX]");
	}
	
	@Sql("/sql/insert_data.sql")
	@Test
	public void testAdd_alreadyExistsAsCurrency() throws Exception {
		// given
		String symbol = "EURUSD=X";
		String name = "EUR/USD";
		String provider = "Provider";
		
		when(lookupService.getDetails(symbol, provider)).thenReturn(new LookupDetails(symbol, name, "EUR", "USD", "CY", provider));
		
		// when
		assertThatThrownBy(() -> adminService.add(symbol, provider))
		.isInstanceOf(AdminException.class)
		.hasMessage("admin.error.currency-already-exists: [EUR]");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testDelete() throws Exception {
		// given
		String providerCode = "RR.L";

		// when
		adminService.delete(-2, true);

		// then
		assertThat(pairService.getForCodeAndProvider(providerCode, "YAHOO")).isNull();
	}

	@Test
	public void testSearch() throws Exception {
		// given
		String symbolOrNameToSearch = "";
		String symbol = "UBI.PA";
		String name = "Ubi";
		String provider = "Provider";
				
		when(lookupService.search(provider)).thenReturn(List.of(new LookupInfo(symbol, name, "Paris", "Titres", provider)));
		when(lookupService.getDetails(symbol, provider)).thenReturn(new LookupDetails(symbol, name, "UBI", "EUR", "EPA", provider));

		// when
		adminService.search(symbolOrNameToSearch);
	}
}
