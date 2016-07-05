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

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.ymanvieu.trading.TradingApplication;
import fr.ymanvieu.trading.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.portofolio.repository.AssetRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(TradingApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_portofolio.sql")
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private AssetRepository assetRepo;

	// FIXME add error cases

	@Test
	public void testBuy() throws Exception {
		String login = "toto";
		String assetCode = "UBI";
		float quantity = 50;

		Order order = orderService.buy(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(1407.75005f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getTotalPrice()).isEqualByComparingTo("1407.75005");

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, EUR);

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(EUR);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("592.24995");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("592.24995");
	}

	@Test
	public void testBuy_Currency() throws Exception {
		String login = "toto";
		String assetCode = USD;
		float quantity = 50;

		Order order = orderService.buy(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(44.1053f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getTotalPrice()).isEqualByComparingTo("44.1053");

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, EUR);

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(EUR);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1955.8947");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("1955.8947");
	}

	@Test
	public void testBuy_Stock() throws Exception {
		String login = "toto";
		String assetCode = "RR";
		float quantity = 5;

		Order order = orderService.buy(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(GBP);
		assertThat(order.getQuantity()).isEqualTo(3460f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getTotalPrice()).isEqualByComparingTo("3460");

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, GBP);

		// EUR->GBP: 0.789587645929174
		// GBP->EUR: 1,266483847810481

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(GBP);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1540");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("1848");
	}

	@Test
	public void testSell() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		int quantity = 50;

		Order order = orderService.sell(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1407.75005f);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo("10");
		assertThat(updatedEntity.getTotalPrice()).isEqualByComparingTo("300");

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, EUR);

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(EUR);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1407.75005");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("1407.75005");
	}

	@Test
	public void testSell_All() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		int quantity = 60;

		Order order = orderService.sell(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1689.300065f);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity).isNull();

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, EUR);

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(EUR);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1689.30006");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("1689.30006");
	}

	@Test
	public void testSell_Stock() throws Exception {
		String login = "toto";
		String assetCode = "MKS";
		float quantity = 5;

		Order order = orderService.sell(login, assetCode, quantity);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(GBP);
		assertThat(order.getValue()).isEqualByComparingTo(2134f);

		AssetEntity updatedEntity = assetRepo.findByUserLoginAndSymbolCode(login, assetCode);

		assertThat(updatedEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo("5");
		assertThat(updatedEntity.getTotalPrice()).isEqualByComparingTo("2134");

		AssetEntity updatedCurrencyEntity = assetRepo.findByUserLoginAndSymbolCode(login, GBP);

		assertThat(updatedCurrencyEntity.getUser().getLogin()).isEqualTo(login);
		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(GBP);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("7134");
		assertThat(updatedCurrencyEntity.getTotalPrice()).isEqualByComparingTo("8702.6765312052");
	}
}
