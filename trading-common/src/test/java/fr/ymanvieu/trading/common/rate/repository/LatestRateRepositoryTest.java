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
package fr.ymanvieu.trading.common.rate.repository;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.rate.FavoriteRate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class LatestRateRepositoryTest {

	@Autowired
	private LatestRateRepository repo;

	@Sql("/sql/insert_data.sql")
	@Sql("/sql/insert_eur_gbp.sql")
	@Test
	public void testDeleteByFromcurCodeAndTocurCode() {
		// when
		int result = repo.deleteByFromcurCodeOrTocurCode(USD);

		// then
		assertThat(result).isEqualTo(3);
		assertThat(repo.count()).isEqualTo(3);
	}

	@Sql("/sql/insert_data.sql")
	@Sql("/sql/insert_favorite_symbol.sql")
	@Test
	public void testFindAllWithFavorites() throws Exception {
		
		List<FavoriteRate> result = repo.findAllWithFavorites(2);
		assertThat(result).hasSize(5)
		.satisfies(fr -> {
			assertThat(fr.getFromcur().getCode()).isEqualTo("BRE");
			assertThat(fr.getTocur().getCode()).isEqualTo("USD");
			assertThat(fr.getFavorite()).isTrue();
		}, atIndex(3));
	}
}
