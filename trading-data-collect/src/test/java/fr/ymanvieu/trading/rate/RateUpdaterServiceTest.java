package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.TestUtils.rate;
import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.rate.entity.RateEntity;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_symbols_bre_usd.sql")
public class RateUpdaterServiceTest {

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private RateService rateService;

	@Autowired
	private SymbolRepository symbolRepo;
	
	@MockBean
	private ApplicationEventPublisher bus;

	private RateUpdaterService rateUpdaterService;
	
	@Autowired
	private HistoricalRateRepository histoRepo;
		
	@Before
	public void setUp() {
		rateUpdaterService = new RateUpdaterService(latestRepo, symbolRepo, rateService, bus);
	}

	private List<Quote> getBrentQuotes() throws Exception {
		List<Quote> quotes = new ArrayList<>();

		quotes.add(quote(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST")));
		quotes.add(quote(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST")));
		quotes.add(quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07 02:00:00.0 CEST")));
		quotes.add(quote(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03 02:00:00.0 CEST")));
		quotes.add(quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02 02:00:00.0 CEST")));

		return quotes;
	}

	@Test
	public void testUpdateRates() throws Exception {
		// given
		List<Quote> quotes = getBrentQuotes();

		RateEntity expected1 = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02 02:00:00.0 CEST"));
		RateEntity expected2 = rate(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03 02:00:00.0 CEST"));
		RateEntity expected3 = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expected4 = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expected5 = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));

		// when
		rateUpdaterService.updateRates(quotes);

		// then
		List<RateEntity> hRates = new ArrayList<>(histoRepo.findAll());
		List<RateEntity> lRates = new ArrayList<>(latestRepo.findAll());

		assertThat(hRates).containsExactlyInAnyOrder(expected1, expected2, expected3, expected4, expected5);

		assertThat(lRates).containsExactly(expected5);

		verify(bus).publishEvent(ArgumentMatchers.<RatesUpdatedEvent>any());
	}

	@Sql("/sql/insert_symbols_bre_usd.sql")
	@Sql("/sql/insert_rates_bre_usd.sql")
	@Test
	public void testUpdateRates_WithExistingData() throws Exception {
		// given
		List<Quote> quotes = getBrentQuotes();

		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09 02:00:00.0 CEST"));
		RateEntity olderButAdded = rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02 02:00:00.0 CEST"));

		// when
		rateUpdaterService.updateRates(quotes);

		// then
		List<RateEntity> hRates = new ArrayList<>(histoRepo.findAll());
		List<RateEntity> lRates = new ArrayList<>(latestRepo.findAll());

		assertThat(hRates).hasSize(7);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest, olderButAdded);

		assertThat(lRates).hasSize(1);
		assertThat(lRates).containsOnly(expectedNewLatest);

		verify(bus).publishEvent(ArgumentMatchers.<RatesUpdatedEvent>any());
	}
}