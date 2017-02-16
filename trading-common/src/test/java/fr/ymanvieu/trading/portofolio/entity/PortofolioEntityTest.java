package fr.ymanvieu.trading.portofolio.entity;

import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

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

import fr.ymanvieu.trading.portofolio.Order;
import fr.ymanvieu.trading.portofolio.OrderException;
import fr.ymanvieu.trading.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;
import fr.ymanvieu.trading.symbol.repository.SymbolRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/sql/insert_portofolio.sql")
public class PortofolioEntityTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private RateService rateService;

	@Autowired
	private PortofolioRepository portofolioRepo;

	// FIXME add error cases

	@Transactional
	@Test
	public void testBuy_WithBaseCurrency() throws Exception {
		String login = "toto";
		String assetCode = "UBI";
		float quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.buy(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(1407.75005f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("1407.75005");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("592.24995");
	}

	@Transactional
	@Test
	public void testBuy_NoFund() throws Exception {
		String login = "seller";
		String assetCode = "BRE";
		float quantity = 1;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		exception.expect(OrderException.class);
		exception.expectMessage("not_enough_fund");

		portofolio.buy(symbolRepo.findOne(assetCode), quantity, rateService);
	}

	@Test
	public void testBuy_NotEnoughFund() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		float quantity = 1;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		exception.expect(OrderException.class);
		exception.expectMessage("not_enough_fund");

		portofolio.buy(symbolRepo.findOne(assetCode), quantity, rateService);
	}

	@Transactional
	@Test
	public void testBuy_Currency() throws Exception {
		String login = "toto";
		String assetCode = USD;
		float quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.buy(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(EUR);
		assertThat(order.getQuantity()).isEqualTo(44.1053f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("44.1053");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1955.8947");
	}

	@Transactional
	@Test
	public void testBuy_Stock() throws Exception {
		String login = "toto";
		String assetCode = "RR";
		float quantity = 5;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.buy(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(GBP);
		assertThat(order.getQuantity()).isEqualTo(3460f);
		assertThat(order.getTo().getCode()).isEqualTo(assetCode);
		assertThat(order.getValue()).isEqualByComparingTo(quantity);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo(new BigDecimal(quantity));
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("3460");

		// EUR->GBP: 0.789587645929174
		// GBP->EUR: 1,266483847810481

		AssetEntity updatedCurrencyEntity = portofolio.getAsset(GBP);

		assertThat(updatedCurrencyEntity.getSymbol().getCode()).isEqualTo(GBP);
		assertThat(updatedCurrencyEntity.getQuantity()).isEqualByComparingTo("1540");
		assertThat(updatedCurrencyEntity.getCurrencyAmount()).isEqualByComparingTo("1848");
	}

	@Transactional
	@Test
	public void testSell_WithBaseCurrency() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		int quantity = 50;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.sell(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1407.75005f);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity.getSymbol().getCode()).isEqualTo(assetCode);
		assertThat(updatedEntity.getQuantity()).isEqualByComparingTo("10");
		assertThat(updatedEntity.getCurrencyAmount()).isEqualByComparingTo("300");

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1407.75005");
	}

	@Transactional
	@Test
	public void testSell_All() throws Exception {
		String login = "seller";
		String assetCode = "UBI";
		int quantity = 60;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.sell(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(EUR);
		assertThat(order.getValue()).isEqualByComparingTo(1689.300065f);

		AssetEntity updatedEntity = portofolio.getAsset(assetCode);

		assertThat(updatedEntity).isNull();

		assertThat(portofolio.getAmount()).isEqualByComparingTo("1689.30006");
	}
	
	@Transactional
	@Test
	public void testSell_Stock() throws Exception {
		String login = "toto";
		String assetCode = "MKS";
		float quantity = 5;

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);

		Order order = portofolio.sell(symbolRepo.findOne(assetCode), quantity, rateService);

		assertThat(order.getFrom().getCode()).isEqualTo(assetCode);
		assertThat(order.getQuantity()).isEqualTo(quantity);
		assertThat(order.getTo().getCode()).isEqualTo(GBP);
		assertThat(order.getValue()).isEqualByComparingTo(2134f);

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

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);
		SymbolEntity se = symbolRepo.findOne(assetCode);

		SymbolEntity currency = portofolio.getCurrencyFor(se);

		assertThat(currency.getCode()).isEqualTo(EUR);
	}

	@Test
	public void testGetCurrencyFor_Stock() {
		String login = "toto";
		String assetCode = "RR";

		PortofolioEntity portofolio = portofolioRepo.findByUserLogin(login);
		SymbolEntity se = symbolRepo.findOne(assetCode);

		SymbolEntity currency = portofolio.getCurrencyFor(se);

		assertThat(currency.getCode()).isEqualTo(GBP);
	}
}
