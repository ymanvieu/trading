package fr.ymanvieu.trading.datacollect.rate;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.common.rate.entity.LatestRate;
import fr.ymanvieu.trading.common.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.common.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.datacollect.config.MapperTestConfig;
import fr.ymanvieu.trading.datacollect.config.RepositoryTestConfig;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({RateUpdaterService.class, RateService.class, MapperTestConfig.class, RepositoryTestConfig.class})
@Sql("/sql/insert_symbols_bre_usd.sql")
public class RateUpdaterServiceTest {
	
	private static final String BRE = "BRE";

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private RateUpdaterService rateUpdaterService;
	
	@Autowired
	private HistoricalRateRepository histoRepo;
	
	@MockBean
	private RatesUpdatedEventListener ratesUpdatedEventListener;

	private List<Quote> getBrentQuotes() {
		List<Quote> quotes = new ArrayList<>();

		quotes.add(new Quote(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00")));

		return quotes;
	}

	@Test
	public void testUpdateRates() {
		// given
		List<Quote> quotes = getBrentQuotes();

		HistoricalRate expected1 = new HistoricalRate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00"));
		HistoricalRate expected2 = new HistoricalRate(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00"));
		HistoricalRate expected3 = new HistoricalRate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00"));
		HistoricalRate expected4 = new HistoricalRate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00"));
		HistoricalRate expected5 = new HistoricalRate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00"));
		LatestRate expected6 = new LatestRate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00"));

		// when
		rateUpdaterService.updateRates(quotes);

		// then
		List<HistoricalRate> hRates = new ArrayList<>(histoRepo.findAll());
		List<LatestRate> lRates = new ArrayList<>(latestRepo.findAll());

		assertThat(hRates).containsExactlyInAnyOrder(expected1, expected2, expected3, expected4, expected5);

		assertThat(lRates).containsExactly(expected6);

		verify(ratesUpdatedEventListener).send(any());
	}

	@Sql("/sql/insert_symbols_bre_usd.sql")
	@Sql("/sql/insert_rates_bre_usd.sql")
	@Test
	public void testUpdateRates_WithExistingData() {
		// given
		List<Quote> quotes = getBrentQuotes();

		HistoricalRate expectedOldLatest = new HistoricalRate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-06T02:00:00"));
		HistoricalRate expectedAdded = new HistoricalRate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00"));
		HistoricalRate expectedNewLatest = new HistoricalRate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00"));
		LatestRate expectedNewLatest1 = new LatestRate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00"));
		HistoricalRate olderButAdded = new HistoricalRate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00"));

		// when
		rateUpdaterService.updateRates(quotes);

		// then
		List<HistoricalRate> hRates = new ArrayList<>(histoRepo.findAll());
		List<LatestRate> lRates = new ArrayList<>(latestRepo.findAll());

		assertThat(hRates).hasSize(7);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest, olderButAdded);

		assertThat(lRates).hasSize(1);
		assertThat(lRates).containsOnly(expectedNewLatest1);

		verify(ratesUpdatedEventListener).send(any());
	}
}
