package fr.ymanvieu.trading.common.portofolio;

import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.EUR;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.GBP;
import static fr.ymanvieu.trading.common.symbol.util.CurrencyUtils.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.offset;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import fr.ymanvieu.trading.common.portofolio.entity.AssetEntity;
import fr.ymanvieu.trading.common.portofolio.repository.PortofolioRepository;
import fr.ymanvieu.trading.common.symbol.Symbol;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@Sql("/sql/insert_portofolio.sql")
public class PortofolioServiceTest {
	
	private static final Offset<Double> PERCENT_OFFSET = offset(0.000000000001d);

	@Autowired
	private PortofolioService portofolioService;

	@Autowired
	private PortofolioRepository portofolioRepo;

	@Sql("/sql/insert_user_symbol.sql")
	@Test
	public void testCreatePortofolio() {
		Integer login = 2;
		String baseCurrency = EUR;
		int amount = 1000;

		Portofolio p = portofolioService.createPortofolio(login, baseCurrency, amount);

		assertThat(p.getBaseCurrency().getSymbol().getCode()).isEqualTo(baseCurrency);
		assertThat(p.getBaseCurrency().getQuantity()).isEqualTo(amount);
	}

	@Test
	public void testGetPortofolio() {
		Integer login = 2;

		// FIXME add asserts

		Portofolio result = portofolioService.getPortofolio(login);

		assertThat(result.getPercentChange()).isCloseTo(13.47978618287961, PERCENT_OFFSET);
		assertThat(result.getValueChange()).isCloseTo(922.8690277999999, PERCENT_OFFSET);

		assertThat(result.getAssets()).extracting("symbol.code", "percentChange", "valueChange")
				.containsExactlyInAnyOrder( //
						tuple("UBI", -6.14999667, -184.4999), //
						tuple("BRE", 39.33333333, 1180d), //
						tuple("GBP", 5.54032065, 66.4838478));
	}

	@Test
	public void testGetAsset() {
		Integer login = 0;
		String symbol = GBP;
		int amount = 5000;

		AssetEntity ae = portofolioRepo.findByUserId(login).getAsset(symbol);

		AssetInfo a = portofolioService.getAsset(ae);

		assertThat(a.getSymbol().getCode()).isEqualTo(symbol);
		assertThat(a.getQuantity()).isEqualTo(amount);
		assertThat(a.getValue()).isEqualTo(6000);
		assertThat(a.getCurrentValue()).isEqualTo(6332.419239);
		assertThat(a.getValueChange()).isEqualTo(332.419239);
		assertThat(a.getPercentChange()).isEqualTo(5.54032065);
		assertThat(a.getCurrentRate()).isEqualTo(1.2664838478);
	}

	@Test
	public void testGetAvailableSymbols_Toto() {
		Integer login = 0;

		List<Symbol> symbols = portofolioService.getAvailableSymbols(login);

		assertThat(symbols).extracting("code").containsExactly("RR", "UBI", USD);
	}

	@Test
	public void testGetOrderInfo_NullSymbol() {
		Integer login = 1;

		assertThatThrownBy(() -> portofolioService.getOrderInfo(login, null, 0))
				.isInstanceOf(NullPointerException.class)
				.hasMessageContaining("symbolCode");
	}

	@Test
	public void testGetOrderInfo_withSymbolOwned() {
		Integer login = 1;
		String symbol = "UBI";

		OrderInfo info = portofolioService.getOrderInfo(login, symbol, 1);

		assertThat(info.getSelected().getSymbol().getCode()).isEqualTo(symbol);
		assertThat(info.getSelectedCurrency().getSymbol().getCode()).isEqualTo(EUR);
		assertThat(info.getGainCost()).isEqualByComparingTo("28.155001");
	}

	@Test
	public void testGetOrderInfo_withSymbolNotOwned() {
		Integer login = 1;
		String symbol = GBP;

		OrderInfo info = portofolioService.getOrderInfo(login, symbol, 1);

		assertThat(info.getSelected().getSymbol().getCode()).isEqualTo(symbol);
		assertThat(info.getSelectedCurrency().getSymbol().getCode()).isEqualTo(EUR);
		assertThat(info.getGainCost()).isEqualByComparingTo("1.2664838478");
	}
}
