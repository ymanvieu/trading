package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.TestUtils.quote;
import static fr.ymanvieu.trading.provider.rate.quandl.Quandl.BRE;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.util.DateUtils.parse;
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
	public void testGetOldestRateDate() throws Exception {
		Date result = rateService.getOldestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-01 22:42:10.0 CET"));
	}

	@Test
	public void testGetOldestRateDate_NoElement() {
		assertThat(rateService.getOldestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetNewestRateDate() throws Exception {
		Date result = rateService.getNewestRateDate(USD, EUR);

		assertThat(result).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Test
	public void testGetNewestRateDate_NoElement() {
		assertThat(rateService.getNewestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetLatest_SameCurrency() throws Exception {

		Quote result = rateService.getLatest(GBP, GBP);

		assertThat(result.getCode()).isEqualTo(GBP);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("1");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Test
	public void testGetLatest_Computed() throws Exception {

		Quote result = rateService.getLatest(EUR, GBP);

		assertThat(result.getCode()).isEqualTo(EUR);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.7556613636");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Test
	public void testGetLatest_Direct() throws Exception {

		Quote result = rateService.getLatest(USD, GBP);

		assertThat(result.getCode()).isEqualTo(USD);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.664982");
		assertThat(result.getTime()).hasSameTimeAs(parse("2015-02-02 08:42:50.0 CET"));
	}

	@Test
	public void testGetLatest() throws Exception {
		// when
		Page<LatestRate> result = rateService.getLatest(USD, EUR, null, null, null, null);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getNumberOfElements()).isEqualTo(1);

		LatestRate r = result.getContent().get(0);

		assertThat(r.getFromcur().getCode()).isEqualTo(USD);
		assertThat(r.getTocur().getCode()).isEqualTo(EUR);
		assertThat(r.getDate()).hasSameTimeAs(parse("2015-01-30 13:55:00.0 CET"));
		assertThat(r.getValue()).isEqualByComparingTo("0.88");
	}

	@Sql("/sql/insert_histo.sql")
	@Test
	public void testAddHistoricalRates() throws Exception {
		// given
		List<Quote> quotes = getBrentQuotes();

		// when
		rateService.addHistoricalRates(quotes);

		// then
		assertThat(histoRepo.findAll()).hasSize(5);
	}
}