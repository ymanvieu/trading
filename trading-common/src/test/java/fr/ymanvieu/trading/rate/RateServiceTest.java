package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.eventbus.EventBus;

import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_data.sql")
public class RateServiceTest {

	@Autowired
	private HistoricalRateRepository histoRepo;

	@MockBean
	private EventBus bus;

	@Autowired
	private RateService rateService;

	@Test
	public void testGetOldestRateDate() {
		Date result = rateService.getOldestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-01T22:42:10+01:00"));
	}

	@Test
	public void testGetOldestRateDate_NoElement() {
		assertThat(rateService.getOldestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetNewestRateDate() {
		Date result = rateService.getNewestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-02T08:42:50+01:00"));
	}

	@Test
	public void testGetNewestRateDate_NoElement() {
		assertThat(rateService.getNewestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetLatest_SameCurrency() {

		Quote result = rateService.getLatest(GBP, GBP);

		assertThat(result.getCode()).isEqualTo(GBP);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("1");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02T08:42:50+01:00"));
	}

	@Test
	public void testGetLatest_Computed() {

		Quote result = rateService.getLatest(EUR, GBP);

		assertThat(result.getCode()).isEqualTo(EUR);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.7556613636");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02T08:42:50+01:00"));
	}

	@Test
	public void testGetLatest_Direct() {

		Quote result = rateService.getLatest(USD, GBP);

		assertThat(result.getCode()).isEqualTo(USD);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.664982");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02T08:42:50+01:00"));
	}

	@Test
	public void testGetLatest() {
		// when
		Page<LatestRate> result = rateService.getLatest(USD, EUR, null, null, null, null);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getNumberOfElements()).isEqualTo(1);

		LatestRate r = result.getContent().get(0);

		assertThat(r.getFromcur().getCode()).isEqualTo(USD);
		assertThat(r.getTocur().getCode()).isEqualTo(EUR);
		assertThat(r.getDate()).hasSameTimeAs(parse("2015-01-30T13:55:00+01:00"));
		assertThat(r.getValue()).isEqualByComparingTo("0.88");
	}

	@Sql("/sql/insert_histo.sql")
	@Test
	public void testAddHistoricalRates() {
		// given
		List<Quote> quotes = new ArrayList<>();
		quotes.add(quote(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00+02:00")));
		quotes.add(quote(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00+02:00")));
		quotes.add(quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00+02:00")));
		quotes.add(quote(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00+02:00")));
		quotes.add(quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00+02:00")));

		// when
		rateService.addHistoricalRates(quotes);

		// then
		assertThat(histoRepo.findAll()).hasSize(5);
	}
}