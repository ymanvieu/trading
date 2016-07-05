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
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SymbolServiceTest {

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private SymbolService symbolService;

	@Test
	public void testAddSymbolOK_notExistingYet() {
		// given
		String code = CurrencyUtils.USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		SymbolEntity result = symbolService.addSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).isEqualTo(symbol(code, name, countryFlag, null));
		assertThat(result).isEqualTo(symbolRepo.findByCode(code));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbolOK_AlreadyExisting() {
		// given
		String code = CurrencyUtils.USD;
		String name = "US Dollar";
		String countryFlag = "us";

		// when
		SymbolEntity result = symbolService.addSymbol(code, name, countryFlag, null);

		// then
		assertThat(result).isEqualTo(symbol(code, name, countryFlag, null));
		assertThat(result).isEqualTo(symbolRepo.findByCode(code));
	}
}
