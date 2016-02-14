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
package fr.ymanvieu.forex.core.websocket;

import static fr.ymanvieu.forex.core.provider.impl.Quandl.BRE;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.forex.core.ForexApplication;
import fr.ymanvieu.forex.core.Utils;
import fr.ymanvieu.forex.core.model.entity.rate.RateEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RatesWebSocketHandlerTest {

	@Autowired
	private RatesWebSocketHandler handler;

	@Test
	public void testSerializeRates() throws Exception {
		// given
		RateEntity rate = Utils.rate(BRE, USD, new BigDecimal("57.8"), DATE_TIME_WITH_TZ.parse("2015-04-07 02:00:00.0 CEST"));

		// when
		String result = handler.serializeRates(Lists.newArrayList(rate));

		// then
		assertThat(result).isEqualTo("[{\"fromcur\":\"BRE\",\"tocur\":\"USD\",\"value\":57.8,\"date\":1428364800000}]");
	}
}
