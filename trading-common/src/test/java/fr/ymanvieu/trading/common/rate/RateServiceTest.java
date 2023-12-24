package fr.ymanvieu.trading.common.rate;

import static fr.ymanvieu.trading.common.symbol.Currency.EUR;
import static fr.ymanvieu.trading.common.symbol.Currency.GBP;
import static fr.ymanvieu.trading.common.symbol.Currency.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.common.config.MapperTestConfig;
import fr.ymanvieu.trading.common.provider.Quote;
import fr.ymanvieu.trading.common.rate.repository.HistoricalRateRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({RateService.class, MapperTestConfig.class})
@Sql("/sql/insert_data.sql")
public class RateServiceTest {
	
	private static final String BRE = "BRE";

	@Autowired
	private HistoricalRateRepository histoRepo;

	@Autowired
	private RateService rateService;

	@Test
	public void testGetOldestRateDate() {
		Instant result = rateService.getOldestRateDate(USD, EUR);

		assertThat(result).isEqualTo(parse("2015-02-01T22:42:10"));
	}

	@Test
	public void testGetOldestRateDate_NoElement() {
		assertThat(rateService.getOldestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetNewestRateDate() {
		Instant result = rateService.getNewestRateDate(USD, EUR);

		assertThat(result).isEqualTo(parse("2015-02-02T08:42:50"));
	}

	@Test
	public void testGetNewestRateDate_NoElement() {
		assertThat(rateService.getNewestRateDate(USD, "TOTO")).isNull();
	}

	@Test
	public void testGetLatest_Computed() {

		Rate result = rateService.getLatest(EUR, GBP);

		assertThat(result.getFromcur().getCode()).isEqualTo(EUR);
		assertThat(result.getTocur().getCode()).isEqualTo(GBP);
		assertThat(result.getValue()).isEqualByComparingTo("0.7556613636");
		assertThat(result.getDate()).isEqualTo(parse("2015-02-02T08:42:50"));
	}

	@Test
	public void testGetLatest_Direct() {

		Rate result = rateService.getLatest(USD, GBP);

		assertThat(result.getFromcur().getCode()).isEqualTo(USD);
		assertThat(result.getTocur().getCode()).isEqualTo(GBP);
		assertThat(result.getValue()).isEqualByComparingTo("0.664982");
		assertThat(result.getDate()).isEqualTo(parse("2015-02-02T08:42:50"));
	}

	@Sql("/sql/insert_histo.sql")
	@Test
	public void testAddHistoricalRates() {
		// given
		List<Quote> quotes = new ArrayList<>();
		quotes.add(new Quote(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00")));
		quotes.add(new Quote(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00")));

		// when
		rateService.addHistoricalRates(quotes);

		// then
		assertThat(histoRepo.findAll()).hasSize(5);
	}
}
