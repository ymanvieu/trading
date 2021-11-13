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
package fr.ymanvieu.trading.common.symbol;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;

@ExtendWith(SpringExtension.class)
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
		Symbol result = symbolService.addSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).isEqualToComparingFieldByField(new SymbolEntity(code, name, countryFlag, null));
		assertThat(result).isEqualToComparingFieldByField(symbolRepo.findById(code).get());
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbol_NotExistWithExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		var flag = "flag";
		String currencyCode = USD;

		// when
		Symbol result = symbolService.addSymbol(code, name, flag, currencyCode);

		// then
		assertThat(result).isEqualToIgnoringGivenFields(new Symbol(code, name, flag, new Symbol(USD, "US Dollar", "us", null)));
		
		var savedSymbol = symbolRepo.findById(code).get();
		assertThat(savedSymbol.getCode()).isEqualTo(code);
		assertThat(savedSymbol.getName()).isEqualTo(name);
		assertThat(savedSymbol.getCountryFlag()).isEqualTo(flag);
		assertThat(savedSymbol.getCurrency().getCode()).isEqualTo(currencyCode);
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
				.hasMessageContaining("Unable to find fr.ymanvieu.trading.common.symbol.entity.SymbolEntity with id XXX");
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
			assertThat(result).isEqualTo(new Symbol(USD, "US Dollar", "us", null));
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
		symbolService.addFavoriteSymbol(USD, EUR, 2);
	}

	@Sql("/sql/insert_user_symbol_favorite.sql")
	@Test
	public void testDeleteFavoriteSymbol() throws Exception {
		symbolService.deleteFavoriteSymbol("UBI", EUR, 2);
	}
}
