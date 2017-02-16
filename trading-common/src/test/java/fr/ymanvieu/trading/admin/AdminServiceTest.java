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
package fr.ymanvieu.trading.admin;

import static fr.ymanvieu.trading.util.DateUtils.parse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import com.querydsl.core.types.dsl.BooleanExpression;

import fr.ymanvieu.trading.provider.LookupDetails;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.LookupService;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.entity.PairEntity;
import fr.ymanvieu.trading.rate.Quote;
import fr.ymanvieu.trading.rate.RateService;
import fr.ymanvieu.trading.rate.StockService;
import fr.ymanvieu.trading.rate.entity.QHistoricalRate;
import fr.ymanvieu.trading.rate.repository.HistoricalRateRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminServiceTest {

	@Autowired
	private PairService pairService;

	@Autowired
	private RateService rateService;

	@MockBean
	private LookupService lookupService;

	@MockBean
	private StockService stock;

	@Autowired
	private AdminService adminService;

	@Autowired
	private HistoricalRateRepository hRateRepo;

	@Test
	public void testAdd() throws Exception {
		// given
		String symbol = "UBI.PA";
		String name = "Ubi";
		String provider = "Provider";

		when(lookupService.search(provider))
				.thenReturn(asList(new LookupInfo(symbol, name, "Paris", "Titres", provider)));
		when(lookupService.getDetails(symbol, provider))
				.thenReturn(new LookupDetails(symbol, name, "UBI", "EUR", provider));

		Quote histoQuote = new Quote(symbol, new BigDecimal("25.2"), parse("2016-06-30 02:00:00.0 CEST"));
		Quote latestQuote = new Quote(symbol, new BigDecimal("25"), parse("2016-06-30 19:32:00.0 CEST"));

		when(stock.getHistoricalRates(symbol)).thenReturn(asList(histoQuote));
		when(stock.getLatestRate(symbol)).thenReturn(latestQuote);

		// when
		SymbolInfo result = adminService.add(symbol, provider);

		// then
		PairEntity pairResult = pairService.getForCode(symbol);

		assertThat(result.getCode()).isEqualTo(pairResult.getSymbol());
		assertThat(result.getName()).isEqualTo(pairResult.getName());
		assertThat(result.getQuote().getCode()).isEqualTo(pairResult.getSource().getCode());
		assertThat(result.getQuote().getCurrency()).isEqualTo(pairResult.getTarget().getCode());

		assertThat(result.getQuote().getPrice()).isEqualTo("25");
		assertThat(result.getQuote().getTime()).isEqualTo(parse("2016-06-30 19:32:00.0 CEST"));

		QHistoricalRate qHistoRate = QHistoricalRate.historicalRate;
		BooleanExpression exp = qHistoRate.fromcur.code.eq("UBI").and(qHistoRate.tocur.code.eq("EUR"));

		assertThat(hRateRepo.count(exp)).isEqualTo(2);
		assertThat(rateService.getLatest("UBI", "EUR")).isNotNull();
	}

	@Sql("/sql/insert_portofolio.sql")
	@Test
	public void testDelete() throws Exception {
		// given
		String code = "RR.L";

		// when
		adminService.delete(code);

		// then
		assertThat(pairService.getForCode(code)).isNull();
	}
}