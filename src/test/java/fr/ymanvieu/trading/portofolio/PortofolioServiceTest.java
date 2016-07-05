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
package fr.ymanvieu.trading.portofolio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.portofolio.Portofolio;
import fr.ymanvieu.trading.portofolio.PortofolioService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_portofolio.sql")
public class PortofolioServiceTest {

	@Autowired
	private PortofolioService portofolioService;

	@Test
	public void testGetPortofolio() {
		String login = "user";

		// FIXME add asserts

		Portofolio result = portofolioService.getPortofolio(login);

		assertThat(result.getPercentChange()).isEqualByComparingTo(13.479787f);
		assertThat(result.getValueChange()).isEqualByComparingTo(922.86914f);

		assertThat(result.getAssets()).extracting("symbol.code", "percentChange", "valueChange").containsExactlyInAnyOrder( //
				tuple("UBI", -6.1499968f, -184.4999f), //
				tuple("BRE", 39.33333333333333f, 1180f), //
				tuple("GBP", 5.540321f, 66.48385f));
	}
}