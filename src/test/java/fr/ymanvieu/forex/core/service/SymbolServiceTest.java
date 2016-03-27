package fr.ymanvieu.forex.core.service;

import static fr.ymanvieu.forex.core.Utils.symbol;
import static fr.ymanvieu.forex.core.util.CurrencyUtils.USD;
import static fr.ymanvieu.forex.core.util.DateUtils.parse;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mysema.query.BooleanBuilder;

import fr.ymanvieu.forex.core.ForexApplication;
import fr.ymanvieu.forex.core.model.Quote;
import fr.ymanvieu.forex.core.model.entity.rate.QHistoricalRate;
import fr.ymanvieu.forex.core.model.entity.rate.QLatestRate;
import fr.ymanvieu.forex.core.model.entity.symbol.QSymbolEntity;
import fr.ymanvieu.forex.core.model.entity.symbol.SymbolEntity;
import fr.ymanvieu.forex.core.model.repositories.HistoricalRateRepository;
import fr.ymanvieu.forex.core.model.repositories.LatestRateRepository;
import fr.ymanvieu.forex.core.model.repositories.SymbolRepository;
import fr.ymanvieu.forex.core.util.CurrencyUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForexApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SymbolServiceTest {

	@Autowired
	private LatestRateRepository latestRepo;

	@Autowired
	private HistoricalRateRepository repo;

	@Autowired
	private SymbolRepository symbolRepo;

	@Autowired
	private SymbolService symbolService;

	@Test
	public void testAddSymbolForCurrencyOK_notExistingYet() {
		// given
		String code = CurrencyUtils.USD;

		// when
		SymbolEntity result = symbolService.addSymbolForCurrency(code);

		// then
		assertThat(result).isEqualTo(symbol(code, "US Dollar", "us", null));
		assertThat(result).isEqualTo(symbolRepo.findByCode(code));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testAddSymbolForCurrencyOK_AlreadyExisting() {
		// given
		String code = CurrencyUtils.USD;

		// when
		SymbolEntity result = symbolService.addSymbolForCurrency(code);

		// then
		assertThat(result).isEqualTo(symbol(code, "US Dollar", "us", null));
		assertThat(result).isEqualTo(symbolRepo.findByCode(code));
	}

	@Sql("/sql/insert_data.sql")
	@Test
	public void testRemoveSymbol() {
		// given
		String code = CurrencyUtils.USD;

		// when
		symbolService.removeSymbol(code);

		// then
		QSymbolEntity qse = QSymbolEntity.symbolEntity;
		assertThat(symbolRepo.count(new BooleanBuilder(qse.code.eq(code)).or(qse.currency.code.eq(code)))).isZero();

		QLatestRate qlr = QLatestRate.latestRate;
		assertThat(latestRepo.count(new BooleanBuilder(qlr.fromcur.code.eq(code)).or(qlr.tocur.code.eq(code)))).isZero();

		QHistoricalRate qhr = QHistoricalRate.historicalRate;
		assertThat(repo.count(new BooleanBuilder(qhr.fromcur.code.eq(code)).or(qhr.tocur.code.eq(code)))).isZero();
	}

	@Test
	public void testAddSymbolOK() throws Exception {
		// given
		String code = "TOTO", currency = USD;
		Quote latestQuote = new Quote(code, null, new BigDecimal("1.2"), parse("2016-03-20 17:34:0.0 CET"));
		latestQuote.setCurrency(currency);
		Quote histoQuote = new Quote(code, null, new BigDecimal("1.3"), parse("2016-03-17 19:00:0.0 CET"));
		histoQuote.setCurrency(currency);

		// when
		SymbolEntity result = symbolService.addSymbol(latestQuote, Arrays.asList(histoQuote));

		// then
		QSymbolEntity qse = QSymbolEntity.symbolEntity;
		SymbolEntity savedSymbol = symbolRepo.findOne(new BooleanBuilder(qse.code.eq(code)).and(qse.currency.code.eq(currency)));
		assertThat(savedSymbol).isEqualTo(symbol(code, null, null, symbol(currency, "US Dollar", "us", null)));
		assertThat(result).isEqualTo(savedSymbol);

		QLatestRate qlr = QLatestRate.latestRate;
		assertThat(latestRepo.count(new BooleanBuilder(qlr.fromcur.code.eq(code)).and(qlr.tocur.code.eq(currency)))).isEqualTo(1);

		QHistoricalRate qhr = QHistoricalRate.historicalRate;
		assertThat(repo.count(new BooleanBuilder(qhr.fromcur.code.eq(code)).and(qhr.tocur.code.eq(currency)))).isEqualTo(2);
	}
}
