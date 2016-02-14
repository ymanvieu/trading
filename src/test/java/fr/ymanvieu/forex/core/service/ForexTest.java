/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.forex.core.service;

import static fr.ymanvieu.forex.core.Utils.rate;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
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

import fr.ymanvieu.forex.core.ForexApplication;
import fr.ymanvieu.forex.core.Utils;
import fr.ymanvieu.forex.core.http.ConnectionHandler;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.provider.impl.Yahoo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ForexTest {

	private DataUpdater updater;

	private Forex forex;

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private HistoricalRateRepository repo;

	@Mock
	private ConnectionHandler handler;

	@InjectMocks
	private Yahoo defaultProvider;

	@Mock
	private EventBus bus;

	private static String RESULT_20141219;

	private static String RESULT_20150920;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RESULT_20141219 = Utils.readFile("/provider/yahoo/quote_20141219.json");
		RESULT_20150920 = Utils.readFile("/provider/yahoo/quote_20150920.json");
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		when(handler.sendGet(anyString())).thenReturn(RESULT_20141219);

		forex = new Forex(defaultProvider);
		updater = new DataUpdater(repo, latestRepo, bus);
	}

	@Test
	public void testUpdateRates_NoExistingData() throws Exception {
		// when
		updater.updateRates(forex.getProvider());

		// then
		verify(bus).post(any());

		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(172).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")));

		assertThat(lRates).hasSize(172).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_WithExistingData() throws IOException, Exception {
		// when
		updater.updateRates(forex.getProvider());

		// then
		verify(bus).post(any());

		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(183) //
				.doesNotContain( //
						rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")))
				.containsOnlyOnce( //
						rate(USD, EUR, new BigDecimal("0.882044"), DATE_TIME_WITH_TZ.parse("2015-02-02 08:42:50.0 CET")));

		assertThat(lRates).hasSize(173) //
				.doesNotContain( //
						rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")))
				.containsOnlyOnce(//
						rate(USD, EUR, new BigDecimal("0.88"), DATE_TIME_WITH_TZ.parse("2015-01-30 13:55:00.0 CET")));
	}

	@Test
	public void testUpdateRates_TwoCallsWithSameData() throws IOException, Exception {
		// when
		updater.updateRates(forex.getProvider());
		updater.updateRates(forex.getProvider());

		// then
		verify(bus).post(any());

		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(172).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")));

		assertThat(lRates).hasSize(172).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")));
	}

	@Test
	public void testUpdateRates_TwoCallsWithNotSameData() throws IOException, Exception {
		when(handler.sendGet(anyString())).thenReturn(RESULT_20141219, RESULT_20150920);

		// when
		updater.updateRates(forex.getProvider());
		updater.updateRates(forex.getProvider());

		// then
		verify(bus, times(2)).post(any());

		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(344).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")), //
				rate(USD, EUR, new BigDecimal("0.884291"), DATE_TIME_WITH_TZ.parse("2015-09-20 18:19:11.0 GMT")));

		assertThat(lRates).hasSize(172).doesNotContain( //
				rate(USD, EUR, new BigDecimal("0.817595"), DATE_TIME_WITH_TZ.parse("2014-12-19 21:40:32.0 GMT")));
		assertThat(lRates).containsOnlyOnce( //
				rate(USD, EUR, new BigDecimal("0.884291"), DATE_TIME_WITH_TZ.parse("2015-09-20 18:19:11.0 GMT")));

	}
}
