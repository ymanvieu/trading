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
import static fr.ymanvieu.forex.core.provider.impl.Quandl.BRE;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import fr.ymanvieu.forex.core.provider.impl.Quandl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OilTest {

	private Oil oil;

	private DataUpdater updater;

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private HistoricalRateRepository repo;

	@Autowired
	private SymbolService symbolService;

	@Mock
	private ConnectionHandler oilHandler;

	@InjectMocks
	private Quandl quandl;

	@Mock
	private EventBus bus;

	private static String BRENT_LIGHT, BRENT_LIGHT_20150918;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BRENT_LIGHT = Utils.readFile("/provider/quandl/brent_v3-light.json");
		BRENT_LIGHT_20150918 = Utils.readFile("/provider/quandl/brent_v3-light_20150918.json");
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		when(oilHandler.sendGet(anyString())).thenReturn(BRENT_LIGHT);

		oil = new Oil(quandl);
		updater = new DataUpdater(repo, latestRepo, symbolService, bus);
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_Oil() throws Exception {
		// given
		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), DATE_TIME_WITH_TZ.parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), DATE_TIME_WITH_TZ.parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("58.3"), DATE_TIME_WITH_TZ.parse("2015-04-08 02:00:00.0 CEST"));

		// when
		updater.updateRates(oil.getProvider());

		// then
		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(13);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(2);
		assertThat(lRates).doesNotContain(expectedOldLatest);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);

		verify(bus).post(any());
	}

	@Test
	public void testUpdateRates_OilNoExistingData() throws Exception {
		// given
		RateEntity expected1 = rate(BRE, USD, new BigDecimal("55.18"), DATE_TIME_WITH_TZ.parse("2015-04-01 02:00:00.0 CEST"));
		RateEntity expected2 = rate(BRE, USD, new BigDecimal("56.72"), DATE_TIME_WITH_TZ.parse("2015-04-02 02:00:00.0 CEST"));
		RateEntity expected3 = rate(BRE, USD, new BigDecimal("55.18"), DATE_TIME_WITH_TZ.parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expected4 = rate(BRE, USD, new BigDecimal("57.8"), DATE_TIME_WITH_TZ.parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expected5 = rate(BRE, USD, new BigDecimal("58.3"), DATE_TIME_WITH_TZ.parse("2015-04-08 02:00:00.0 CEST"));

		// when
		updater.updateRates(oil.getProvider());

		// then
		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(5);
		assertThat(hRates).containsOnlyOnce(expected1, expected2, expected3, expected4, expected5);

		assertThat(lRates).hasSize(1);
		assertThat(lRates).containsOnlyOnce(expected5);

		verify(bus).post(any());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_OilTwoCallsWithSameData() throws Exception {
		// given
		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), DATE_TIME_WITH_TZ.parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), DATE_TIME_WITH_TZ.parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("58.3"), DATE_TIME_WITH_TZ.parse("2015-04-08 02:00:00.0 CEST"));

		// when
		updater.updateRates(oil.getProvider());

		// then
		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(13);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(2);
		assertThat(lRates).doesNotContain(expectedOldLatest);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);

		verify(bus).post(any());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testUpdateRates_OilTwoCallsWithNotSameData() throws Exception {
		when(oilHandler.sendGet(anyString())).thenReturn(BRENT_LIGHT, BRENT_LIGHT_20150918);

		// given
		RateEntity expectedOldLatest = rate(BRE, USD, new BigDecimal("55.18"), DATE_TIME_WITH_TZ.parse("2015-04-06 02:00:00.0 CEST"));
		RateEntity expectedAdded = rate(BRE, USD, new BigDecimal("57.8"), DATE_TIME_WITH_TZ.parse("2015-04-07 02:00:00.0 CEST"));
		RateEntity expectedFirstCallLatest = rate(BRE, USD, new BigDecimal("58.3"), DATE_TIME_WITH_TZ.parse("2015-04-08 02:00:00.0 CEST"));
		RateEntity expectedNewLatest = rate(BRE, USD, new BigDecimal("49.26"), DATE_TIME_WITH_TZ.parse("2015-09-18 02:00:00.0 CEST"));

		// when
		updater.updateRates(oil.getProvider());
		updater.updateRates(oil.getProvider());

		// then
		List<? extends RateEntity> hRates = repo.findAll();
		List<? extends RateEntity> lRates = latestRepo.findAll();

		assertThat(hRates).hasSize(130);
		assertThat(hRates).containsOnlyOnce(expectedOldLatest, expectedFirstCallLatest, expectedAdded, expectedNewLatest);

		assertThat(lRates).hasSize(2);
		assertThat(lRates).containsOnlyOnce(expectedNewLatest);
		assertThat(lRates).doesNotContain(expectedOldLatest, expectedFirstCallLatest, expectedAdded);

		verify(bus, times(2)).post(any());
	}
}