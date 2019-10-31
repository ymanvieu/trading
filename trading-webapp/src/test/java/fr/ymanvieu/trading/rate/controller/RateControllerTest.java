/**
 * Copyright (C) 2018 Yoann Manvieu
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
package fr.ymanvieu.trading.rate.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import fr.ymanvieu.trading.config.TradingWebAppConfig;
import fr.ymanvieu.trading.config.WebSecurityTestConfig;
import fr.ymanvieu.trading.rate.DateValue;
import fr.ymanvieu.trading.rate.DateValueImpl;
import fr.ymanvieu.trading.rate.FavoriteRate;
import fr.ymanvieu.trading.rate.Rate;
import fr.ymanvieu.trading.rate.RateService;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(RateController.class)
@ContextConfiguration(classes = { TradingWebAppConfig.class, WebSecurityTestConfig.class })
public class RateControllerTest {
	
	@MockBean
	private RateService rateService;

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void testFindRawValues() throws Exception {
		// GIVEN
		List<DateValue> histo = asList(
				new DateValueImpl().setDate(Instant.now()).setValue(123.789d),
				new DateValueImpl().setDate(Instant.now()).setValue(12d));
		
		when(rateService.getHistoricalValues(eq("USD"), eq("EUR"), any(), any())).thenReturn(histo);
		
		// WHEN
		mvc.perform(get("/api/rate/raw")
				.param("fromcur", "USD")
				.param("tocur", "EUR"))
		.andExpect(jsonPath("$[0][0]").value(histo.get(0).getDate().toEpochMilli()))
		.andExpect(jsonPath("$[0][1]").value(histo.get(0).getValue()))
		.andExpect(jsonPath("$[1][0]").value(histo.get(1).getDate().toEpochMilli()))
		.andExpect(jsonPath("$[1][1]").value(histo.get(1).getValue()));
	}

	@Test
	public void testFindLatestRate() throws Exception {
		// GIVEN
		Rate rate = new Rate("UBI", "EUR", new BigDecimal(123.789d), Instant.now());
		
		when(rateService.getLatest(eq("UBI"), eq("EUR"))).thenReturn(rate);

		// WHEN
		mvc.perform(get("/api/rate/latest")
				.param("fromcur", "UBI")
				.param("tocur", "EUR"))
				.andExpect(jsonPath("$.fromcur.code").value(rate.getCode()))
				.andExpect(jsonPath("$.tocur.code").value(rate.getCurrency()))
				.andExpect(jsonPath("$.value").value(rate.getPrice()))
				.andExpect(jsonPath("$.date").value(rate.getTime().toEpochMilli()));
	}

	@WithMockUser
	@Test
	public void testFindAllLatest_authenticated() throws Exception {
		// GIVEN
		FavoriteRate rate = mock(FavoriteRate.class);
		when(rate.getFavorite()).thenReturn(true);
		
		when(rateService.getAllLatestWithFavorites(eq("user"))).thenReturn(asList(rate));

		// WHEN
		mvc.perform(get("/api/rate"))
				.andExpect(jsonPath("$[0].favorite").value(true));
	}
	
	@Test
	public void testFindAllLatest_anonymous() throws Exception {
		// GIVEN
		FavoriteRate rate = mock(FavoriteRate.class);
		when(rateService.getAllLatestWithFavorites(isNull())).thenReturn(asList(rate));

		// WHEN
		mvc.perform(get("/api/rate"))
				.andExpect(jsonPath("$[0].favorite").value(false));
	}
}
