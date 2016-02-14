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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.forex.core.ForexApplication;
import fr.ymanvieu.forex.core.Utils;
import fr.ymanvieu.forex.core.http.ConnectionHandler;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.provider.impl.EuropeanCentralBank;
import fr.ymanvieu.forex.core.provider.impl.Quandl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HistoricalTest {

	private Historical histo;

	@Autowired
	private HistoricalRateRepository repo;

	@InjectMocks
	private Quandl quandl;

	@InjectMocks
	private EuropeanCentralBank ecb;

	@Mock
	private ConnectionHandler handler;

	private static String MOCK_BRENT, MOCK_HIST;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MOCK_BRENT = Utils.readFile("/provider/quandl/brent_v3-light.json");
		MOCK_HIST = Utils.readFile("/provider/ecb/eurofxref-daily.xml");
	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		when(handler.sendGet(anyString())).thenReturn(MOCK_BRENT, MOCK_HIST, MOCK_BRENT, MOCK_HIST);

		histo = new Historical(repo, quandl, ecb);
	}

	@Test
	public void testUpdateRates() throws IOException {
		histo.addHistory();

		assertThat(repo.findAll()).hasSize(37);
	}

	@Test
	public void testUpdateRates_TwoCalls() throws IOException {
		histo.addHistory();
		histo.addHistory();

		// FIXME do not add duplicates
		assertThat(repo.findAll()).hasSize(74);
	}
}
