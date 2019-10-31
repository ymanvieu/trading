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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.rate.entity.HistoricalRate;
import fr.ymanvieu.trading.rate.entity.LatestRate;
import fr.ymanvieu.trading.rate.event.RatesUpdatedEvent;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;
import fr.ymanvieu.trading.rate.repository.LatestRateRepository;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@DataJpaTest(properties = "spring.flyway.enabled=false")
@Transactional
@Import(RateService.class)
@Sql("/sql/insert_symbols_bre_usd.sql")
public class RateUpdaterServiceTest {
	
	private static final String BRE = "BRE";

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

	private List<Rate> getBrentQuotes() {
		List<Rate> quotes = new ArrayList<>();

		quotes.add(new Rate(BRE, USD, new BigDecimal("58.3"), parse("2015-04-09T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("57.8"), parse("2015-04-08T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-07T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("56.72"), parse("2015-04-03T02:00:00")));
		quotes.add(new Rate(BRE, USD, new BigDecimal("55.18"), parse("2015-04-02T02:00:00")));

		return quotes;
	}

	@Test
	public void testUpdateRates() {
		// given
		List<Rate> quotes = getBrentQuotes();

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

		verify(bus).publishEvent(ArgumentMatchers.<RatesUpdatedEvent>any());
	}

	@Sql("/sql/insert_symbols_bre_usd.sql")
	@Sql("/sql/insert_rates_bre_usd.sql")
	@Test
	public void testUpdateRates_WithExistingData() {
		// given
		List<Rate> quotes = getBrentQuotes();

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

		verify(bus).publishEvent(ArgumentMatchers.<RatesUpdatedEvent>any());
	}
}