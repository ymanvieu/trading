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
package fr.ymanvieu.trading.symbol;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SymbolServiceTest {

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private SymbolService symbolService;

	@Test
	public void testAddSymbol_notExist() {
		// given
		String code = USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		SymbolEntity result = symbolService.addSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).isEqualTo(new SymbolEntity(code, name, countryFlag, null));
		assertThat(result).isEqualTo(symbolRepo.findById(code).get());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbol_NotExistWithExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		String currencyCode = USD;

		// when
		SymbolEntity result = symbolService.addSymbol(code, name, null, currencyCode);

		// then
		assertThat(result).isEqualTo(new SymbolEntity(code, name, null, new SymbolEntity(USD, "US Dollar", "us", null)));
		assertThat(result).isEqualTo(symbolRepo.findById(code).get());
	}

	@Test
	public void testAddSymbol_NotExistWithNotExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		String currencyCode = "XXX";

		// when
		assertThatThrownBy(() -> symbolService.addSymbol(code, name, null, currencyCode))
				.hasRootCauseInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Unable to find fr.ymanvieu.trading.symbol.entity.SymbolEntity with id XXX");
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbol_AlreadyExist() {
		// given
		String code = USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		assertThatThrownBy(() -> symbolService.addSymbol(code, name, countryFlag, null))
				.isInstanceOf(SymbolException.class)
				.hasMessage("symbols.error.already_exists: [USD]");

	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetForCode() {
		assertThat(symbolService.getForCode(USD))
		.hasValueSatisfying((result) -> {
			assertThat(result).isEqualTo(new SymbolEntity(USD, "US Dollar", "us", null));
			assertThat(result).isEqualToIgnoringGivenFields(symbolRepo.findById(USD).get(), "currencyCode");
		});
	}

	@Test
	public void testGetForCode_NotExist() {
		assertThat(symbolService.getForCode(USD)).isNotPresent();
	}

	@Sql("/sql/insert_user_symbol_favorite.sql")
	@Test
	public void testAddFavoriteSymbol() throws Exception {
		symbolService.addFavoriteSymbol(USD, EUR, "user");
	}

	@Sql("/sql/insert_user_symbol_favorite.sql")
	@Test
	public void testDeleteFavoriteSymbol() throws Exception {
		symbolService.deleteFavoriteSymbol("UBI", EUR, "user");
	}
}
