/**
 * Copyright (C) 2016 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.rate;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static fr.ymanvieu.trading.test.time.DateParser.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.EventBus;

import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;

@RunWith(SpringRunner.class)
@DataJpaTest(properties = "spring.flyway.enabled=false")
@Import(RateService.class)
@Transactional
@Sql("/sql/insert_data.sql")
public class RateServiceTest {
	
	private static final String BRE = "BRE";

	@Autowired
	private HistoricalRateRepository histoRepo;

	@MockBean
	private EventBus bus;

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
	public void testGetLatest_SameCurrency() {

		Rate result = rateService.getLatest(GBP, GBP);

		assertThat(result.getCode()).isEqualTo(GBP);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("1");
		assertThat(result.getTime()).isNull();
	}

	@Test
	public void testGetLatest_Computed() {

		Rate result = rateService.getLatest(EUR, GBP);

		assertThat(result.getCode()).isEqualTo(EUR);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.7556613636");
		assertThat(result.getTime()).isEqualTo(parse("2015-02-02T08:42:50"));
	}

	@Test
	public void testGetLatest_Direct() {

		Rate result = rateService.getLatest(USD, GBP);

		assertThat(result.getCode()).isEqualTo(USD);
		assertThat(result.getCurrency()).isEqualTo(GBP);
		assertThat(result.getPrice()).isEqualByComparingTo("0.664982");
		assertThat(result.getTime()).isEqualTo(parse("2015-02-02T08:42:50"));
	}

	@Sql("/sql/insert_histo.sql")
	@Test
	public void testAddHistoricalRates() {
		// given
		List<Rate> quotes = new ArrayList<>();
		quotes.add(new Rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00")));

		// when
		rateService.addHistoricalRates(quotes);

		// then
		assertThat(histoRepo.findAll()).hasSize(5);
	}
}