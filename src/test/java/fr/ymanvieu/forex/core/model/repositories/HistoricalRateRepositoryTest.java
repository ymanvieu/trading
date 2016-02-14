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
package fr.ymanvieu.forex.core.model.repositories;

import static fr.ymanvieu.forex.core.util.CurrencyUtils.EUR;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.DATE_TIME_WITH_TZ;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.forex.core.ForexApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HistoricalRateRepositoryTest {

	@Autowired
	private HistoricalRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Test
	public void testFindDateValues() throws Exception {
		// given
		Date start = DATE_TIME_WITH_TZ.parse("2015-02-01 00:15:00.0 CET");
		Date end = DATE_TIME_WITH_TZ.parse("2015-02-02 15:59:00.0 CET");

		// when
		List<Object[]> result = repo.findDateValues(USD, EUR, start, end);

		// then
		assertThat(result).hasSize(2);

		Object[] rate0 = result.get(0);
		assertThat((Date) rate0[0]).hasSameTimeAs(DATE_TIME_WITH_TZ.parse("2015-02-01 22:42:10.0 CET"));
		assertThat((BigDecimal) rate0[1]).isEqualByComparingTo("0.883353");

		Object[] rate1 = result.get(1);
		assertThat((Date) rate1[0]).hasSameTimeAs(DATE_TIME_WITH_TZ.parse("2015-02-02 08:42:50.0 CET"));
		assertThat((BigDecimal) rate1[1]).isEqualByComparingTo("0.882044");
	}
}
