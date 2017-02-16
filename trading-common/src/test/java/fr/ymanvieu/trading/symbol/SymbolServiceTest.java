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

import static fr.ymanvieu.trading.TestUtils.symbol;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SymbolServiceTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

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
		assertThat(result).isEqualTo(symbol(code, name, countryFlag, null));
		assertThat(result).isEqualTo(symbolRepo.findOne(code));
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
		assertThat(result).isEqualTo(symbol(code, name, null, symbol(USD, "US Dollar", "us", null)));
		assertThat(result).isEqualTo(symbolRepo.findOne(code));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbol_NotExistWithNotExistingCurrency() {
		// given
		String code = "TOTO";
		String name = "toto";
		String currencyCode = "XXX";

		// expect
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("currencyCode");

		// when
		symbolService.addSymbol(code, name, null, currencyCode);
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbol_AlreadyExist() {
		// given
		String code = USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		SymbolEntity result = symbolService.addSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).isEqualTo(symbol(code, name, countryFlag, null));
		assertThat(result).isEqualTo(symbolRepo.findOne(code));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testGetForCode() {
		SymbolEntity result = symbolService.getForCode(USD);

		assertThat(result).isEqualTo(symbol(USD, "US Dollar", "us"));
		assertThat(result).isEqualToIgnoringGivenFields(symbolRepo.findOne(USD), "currencyCode");
	}

	@Test
	public void testGetForCode_NotExist() {
		SymbolEntity result = symbolService.getForCode(USD);

		assertThat(result).isNull();
	}
}
