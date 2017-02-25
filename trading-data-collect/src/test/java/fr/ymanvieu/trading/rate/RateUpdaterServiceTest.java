package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
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

	private List<Quote> getBrentQuotes() {
		List<Quote> quotes = new ArrayList<>();

		quotes.add(new Quote(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00+02:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00+02:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00+02:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00+02:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00+02:00")));

		return quotes;
	}

	@Test
	public void testUpdateRates() {
		// given
		List<Quote> quotes = getBrentQuotes();

		RateEntity expected1 = new RateEntity(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00+02:00"));
		RateEntity expected2 = new RateEntity(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00+02:00"));
		RateEntity expected3 = new RateEntity(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00+02:00"));
		RateEntity expected4 = new RateEntity(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00+02:00"));
		RateEntity expected5 = new RateEntity(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00+02:00"));

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
	public void testUpdateRates_WithExistingData() {
		// given
		List<Quote> quotes = getBrentQuotes();

		RateEntity expectedOldLatest = new RateEntity(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06T02:00:00+02:00"));
		RateEntity expectedAdded = new RateEntity(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00+02:00"));
		RateEntity expectedNewLatest = new RateEntity(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00+02:00"));
		RateEntity olderButAdded = new RateEntity(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00+02:00"));

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