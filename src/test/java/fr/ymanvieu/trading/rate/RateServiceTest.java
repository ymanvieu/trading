package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.TestUtils.rate;
import static fr.ymanvieu.trading.TestUtils.readFile;
import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.eventbus.EventBus;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.http.ConnectionHandler;
import fr.ymanvieu.trading.provider.rate.ecb.EuropeanCentralBank;
import fr.ymanvieu.trading.provider.rate.quandl.Quandl;
import fr.ymanvieu.trading.provider.rate.yahoo.Yahoo;
import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.SymbolService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RateServiceTest {

	private static String QUANDL_BRENT;

	private static String QUANDL_BRENT_LIGHT_20150918;

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private HistoricalRateRepository histoRepo;

	@Autowired
	private SymbolService symbolService;

	@Mock
	private ConnectionHandler handler;

	@InjectMocks
	private Yahoo yahooProvider;

	@InjectMocks
	private Quandl quandlProvider;

	@InjectMocks
	private EuropeanCentralBank ecbProvider;

	@Mock
	private EventBus bus;

	private RateService rateService;

	// FIXME refactor : duplicated tests between providers

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QUANDL_BRENT = readFile("/provider/quandl/brent_20151222_5days.json");
		QUANDL_BRENT_LIGHT_20150918 = readFile("/rates/brent_v3-light_20150918.json");
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		rateService = new RateService(histoRepo, latestRepo, symbolService, bus);
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetOldestRateDate() throws Exception {
		Date result = rateService.getOldestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-01 22:42:10.0 CET"));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetOldestRateDateOK_NoElement() {
		assertThat(rateService.getOldestRateDate(USD, "TOTO")).isNull();
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetNewestRateDate() throws Exception {
		Date result = rateService.getNewestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetNewestRateDateOK_NoElement() {
		assertThat(rateService.getNewestRateDate(USD, "TOTO")).isNull();
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetLatestOK_SameCurrency() throws Exception {

		Quote result = rateService.getLatest(GBP, GBP);

		assertThat(result.getCode()).isEqualTo(GBP);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("1");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetLatestOK_Computed() throws Exception {

		Quote result = rateService.getLatest(EUR, GBP);

		assertThat(result.getCode()).isEqualTo(EUR);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.7556613636");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetLatestOK_Direct() throws Exception {

		Quote result = rateService.getLatest(USD, GBP);

		assertThat(result.getCode()).isEqualTo(USD);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.664982");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Sql("/sql/insert_histo.sql")
	@Test
	public void testAddHistoricalRates() throws IOException {
		// given
		when(handler.sendGet(anyString())).thenReturn(QUANDL_BRENT);

		// when
		rateService.addHistoricalRates(quandlProvider);

		// then
		assertThat(histoRepo.findAll()).hasSize(5);
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_Quandl() throws Exception {
		// given
		when(handler.sendGet(anyString())).thenReturn(QUANDL_BRENT);

		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));

		// when
		rateService.updateRates(quandlProvider.getRates());

		// then
		List<? extends RateEntity> hRates = histoRepo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(14);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(3);
		assertThat(lRates).doesNotContain(expectedOldLatest);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);

		verify(bus).post(any());
	}

	@Test
	public void testUpdateRates_Quandl_NoExistingData() throws Exception {
		// given
		when(handler.sendGet(anyString())).thenReturn(QUANDL_BRENT);

		RateEntity expected1 = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02 02:00:00.0 CEST"));
		RateEntity expected2 = rate(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03 02:00:00.0 CEST"));
		RateEntity expected3 = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expected4 = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expected5 = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));

		// when
		rateService.updateRates(quandlProvider.getRates());

		// then
		List<? extends RateEntity> hRates = histoRepo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(5);
		assertThat(hRates).containsOnlyOnce(expected1, expected2, expected3, expected4, expected5);

		assertThat(lRates).hasSize(1);
		assertThat(lRates).containsOnlyOnce(expected5);

		verify(bus).post(any());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_Quandl_TwoCallsWithSameData() throws Exception {
		// given
		when(handler.sendGet(anyString())).thenReturn(QUANDL_BRENT);

		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));

		// when
		rateService.updateRates(quandlProvider.getRates());

		// then
		List<? extends RateEntity> hRates = histoRepo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(14);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(3);
		assertThat(lRates).doesNotContain(expectedOldLatest);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);

		verify(bus).post(any());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_Quandl_TwoCallsWithNotSameData() throws Exception {
		when(handler.sendGet(anyString())).thenReturn(QUANDL_BRENT, QUANDL_BRENT_LIGHT_20150918);

		// given
		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expectedFirstCallLatest = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("49.26"), parse("2015-09-19 02:00:00.0 CEST"));

		// when
		rateService.updateRates(quandlProvider.getRates());
		rateService.updateRates(quandlProvider.getRates());

		// then
		List<? extends RateEntity> hRates = histoRepo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(131);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedFirstCallLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(3);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);
		assertThat(lRates).doesNotContain(expectedOldLatest, expectedFirstCallLatest, expectedAdded);

		verify(bus, times(2)).post(any());
	}
}