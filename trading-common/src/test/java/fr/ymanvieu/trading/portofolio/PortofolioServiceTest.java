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

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_portofolio.sql")
public class PortofolioServiceTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Autowired
	private PortofolioService portofolioService;

	@Autowired
	private PortofolioRepository portofolioRepo;

	@Sql("/sql/insert_user_symbol.sql")
	@Test
	public void testCreatePortofolio() {
		String login = "user";
		String baseCurrency = EUR;
		int amount = 1000;

		Portofolio p = portofolioService.createPortofolio(login, baseCurrency, amount);

		assertThat(p.getBaseCurrency().getSymbol().getCode()).isEqualTo(baseCurrency);
		assertThat(p.getBaseCurrency().getQuantity()).isEqualTo(amount);
	}

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

	@Transactional
	@Test
	public void testGetAsset() {
		String login = "toto";
		String symbol = GBP;
		int amount = 5000;

		AssetEntity ae = portofolioRepo.findByUserLogin(login).getAsset(symbol);

		AssetInfo a = portofolioService.getAsset(ae);

		assertThat(a.getSymbol().getCode()).isEqualTo(symbol);
		assertThat(a.getQuantity()).isEqualTo(amount);
		assertThat(a.getValue()).isEqualTo(6000);
		assertThat(a.getCurrentValue()).isEqualTo(6332.419239052405f);
		assertThat(a.getValueChange()).isEqualTo(332.419239052405f);
		assertThat(a.getPercentChange()).isEqualTo(5.540320650873417f);
		assertThat(a.getCurrentRate()).isEqualTo(1.266483847810481f);
	}

	@Test
	public void testGetAvailableSymbols_Toto() {
		String login = "toto";

		List<SymbolEntity> symbols = portofolioService.getAvailableSymbols(login);

		assertThat(symbols).extracting("code").containsExactly("RR", "UBI", USD);
	}

	@Test
	public void testGetInfo_NullSymbol() throws SymbolException {
		String login = "seller";

		exception.expect(NullPointerException.class);
		exception.expectMessage("symbolCode");

		portofolioService.getInfo(login, null, 0);
	}

	@Test
	public void testGetInfo_withSymbolOwned() throws SymbolException {
		String login = "seller";
		String symbol = "UBI";

		OrderInfo info = portofolioService.getInfo(login, symbol, 1);

		assertThat(info.getSelected().getSymbol().getCode()).isEqualTo(symbol);
		assertThat(info.getSelectedCurrency().getSymbol().getCode()).isEqualTo(EUR);
		assertThat(info.getGainCost()).isEqualByComparingTo("28.155001");
	}

	@Test
	public void testGetInfo_withSymbolNotOwned() throws SymbolException {
		String login = "seller";
		String symbol = GBP;

		OrderInfo info = portofolioService.getInfo(login, symbol, 1);

		assertThat(info.getSelected().getSymbol().getCode()).isEqualTo(symbol);
		assertThat(info.getSelectedCurrency().getSymbol().getCode()).isEqualTo(EUR);
		assertThat(info.getGainCost()).isEqualByComparingTo("1.2664838478");
	}
}