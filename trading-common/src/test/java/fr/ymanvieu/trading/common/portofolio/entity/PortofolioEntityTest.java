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
package fr.ymanvieu.trading.common.portofolio.entity;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.portofolio.Order;
import fr.ymanvieu.trading.common.portofolio.OrderException;
import fr.ymanvieu.trading.common.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.common.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql("/sql/insert_portofolio.sql")
public class PortofolioEntityTest {

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;

	@Autowired
	private PortofolioRepository portofolioRepo;

	// FIXME add error cases

	@Test
	public void testBuy_WithBaseCurrency() throws Exception {
		String login = "toto";
		String assetCode = "UBI";
		double quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.buy(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(1407.75005);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("1407.75005");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("592.24995");
	}

	@Test
	public void testBuy_NoFund() throws Exception {
		String login = "seller";
		String assetCode = "BRE";
		double quantity = 1;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		assertThatExceptionOfType(OrderException.class)
			.isThrownBy(() -> portofolio.buy(symbolRepo.findById(assetCode).get(), quantity, rateService))
			.withMessageContaining("not_enough_fund");
		
	}

	@Test
	public void testBuy_NotEnoughFund() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		double quantity = 1;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		assertThatExceptionOfType(OrderException.class)
			.isThrownBy(() -> portofolio.buy(symbolRepo.findById(assetCode).get(), quantity, rateService))
			.withMessageContaining("not_enough_fund");
	}

	@Test
	public void testBuy_Currency() throws Exception {
		String login = "toto";
		String assetCode = USD;
		double quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.buy(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(44.1053);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("44.1053");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1955.8947");
	}

	@Test
	public void testBuy_Stock() throws Exception {
		String login = "toto";
		String assetCode = "RR";
		double quantity = 5;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.buy(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(GBP);
		assertThat(order.getQuantity()).isEqualTo(3460f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(BigDecimal.valueOf(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("3460");

		// EUR->GBP: 0.789587645929174
		// GBP->EUR: 1,266483847810481

		AssetEntity updatedCurrencyEntity = portofolio.getAsset(GBP);

		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(GBP);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1540");
		assertThat(updatedCurrencyEntity.getCurrencyAmount()).isEqualByComparingTo("1848");
	}

	@Test
	public void testSell_WithBaseCurrency() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		double quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.sell(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1407.75005d);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo("10");
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("300");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1407.75005");
	}

	@Test
	public void testSell_All() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		double quantity = 60;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.sell(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1689.30006);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity).isNull();

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1689.30006");
	}
	
	@Test
	public void testSell_Stock() throws Exception {
		String login = "toto";
		String assetCode = "MKS";
		double quantity = 5;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);

		Order order = portofolio.sell(symbolRepo.findById(assetCode).get(), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(GBP);
		assertThat(order.getValue()).isEqualByComparingTo(2134d);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo("5");
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("2134");

		AssetEntity updatedCurrencyEntity = portofolio.getAsset(GBP);

		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(GBP);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("7134");
		assertThat(updatedCurrencyEntity.getCurrencyAmount()).isEqualByComparingTo("8702.6765312052");
	}

	@Test
	public void testGetCurrencyFor_Currency() {
		String login = "toto";
		String assetCode = USD;

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);
		SymbolEntity se = symbolRepo.findById(assetCode).get();

		SymbolEntity currency = portofolio.getCurrencyFor(se);

		assertThat(currency.getCode()).isEqualTo(EUR);
	}

	@Test
	public void testGetCurrencyFor_Stock() {
		String login = "toto";
		String assetCode = "RR";

		PortofolioEntity portofolio = portofolioRepo.findByUserUsername(login);
		SymbolEntity se = symbolRepo.findById(assetCode).get();

		SymbolEntity currency = portofolio.getCurrencyFor(se);

		assertThat(currency.getCode()).isEqualTo(GBP);
	}
}
