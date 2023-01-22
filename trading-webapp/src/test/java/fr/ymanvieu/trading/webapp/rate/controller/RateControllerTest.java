package fr.ymanvieu.trading.webapp.rate.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import fr.ymanvieu.trading.common.rate.DateValue;
import fr.ymanvieu.trading.common.rate.DateValueImpl;
import fr.ymanvieu.trading.common.rate.FavoriteRate;
import fr.ymanvieu.trading.common.rate.Rate;
import fr.ymanvieu.trading.common.rate.RateService;
import fr.ymanvieu.trading.common.symbol.Symbol;
import fr.ymanvieu.trading.test.config.MapperTestConfig;
import fr.ymanvieu.trading.test.config.WebSecurityTestConfig;
import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@Import({ RateController.class, MapperTestConfig.class })
@ContextConfiguration(classes = { TradingWebAppConfig.class, WebSecurityTestConfig.class })
public class RateControllerTest {
	
	@MockBean
	private RateService rateService;

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void testFindHistoricalValues() throws Exception {
		// GIVEN
		List<DateValue> histo = asList(
				new DateValueImpl().setDate(Instant.now()).setValue(123.789d),
				new DateValueImpl().setDate(Instant.now()).setValue(12d));
		
		when(rateService.getHistoricalValues(eq("USD"), eq("EUR"), any(), any())).thenReturn(histo);
		
		// WHEN
		mvc.perform(get("/api/rate/history")
				.param("fromcur", "USD")
				.param("tocur", "EUR"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0][0]").value(histo.get(0).getDate().toEpochMilli()))
		.andExpect(jsonPath("$[0][1]").value(histo.get(0).getValue()))
		.andExpect(jsonPath("$[1][0]").value(histo.get(1).getDate().toEpochMilli()))
		.andExpect(jsonPath("$[1][1]").value(histo.get(1).getValue()));
	}

    @Test
    public void testFindLatest() throws Exception {
        // GIVEN
        var eur = new Symbol("EUR", "eur", "eu", null);
        var ubi = new Symbol("UBI", "ubisoft", "fr", eur);
        var rate = new Rate(ubi, eur, new BigDecimal("123.789"), Instant.now());

        when(rateService.getLatest(eq("UBI"), eq("EUR"))).thenReturn(rate);

        // WHEN
        mvc.perform(get("/api/rate/latest")
                .param("fromcur", "UBI")
                .param("tocur", "EUR"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fromcur.code").value(rate.getFromcur().getCode()))
        .andExpect(jsonPath("$.fromcur.name").value(rate.getFromcur().getName()))
        .andExpect(jsonPath("$.fromcur.countryFlag").value(rate.getFromcur().getCountryFlag()))
        .andExpect(jsonPath("$.tocur.code").value(rate.getTocur().getCode()))
        .andExpect(jsonPath("$.tocur.name").value(rate.getTocur().getName()))
        .andExpect(jsonPath("$.tocur.countryFlag").value(rate.getTocur().getCountryFlag()))
        .andExpect(jsonPath("$.value").value(rate.getValue()))
        .andExpect(jsonPath("$.date").value(rate.getDate().toEpochMilli()));
    }

	@WithMockUser("1")
	@Test
	public void testFindAllLatest_authenticated() throws Exception {
		// GIVEN
		var from = mock(FavoriteRate.Symbol.class);
		when(from.getCode()).thenReturn("UBI");
		when(from.getName()).thenReturn("ubisoft");
		when(from.getCountryFlag()).thenReturn("fr");
		
		var to = mock(FavoriteRate.Symbol.class);
		when(to.getCode()).thenReturn("EUR");
		when(to.getName()).thenReturn("euro");
		when(to.getCountryFlag()).thenReturn("eu");
		
		FavoriteRate rate = mock(FavoriteRate.class);
		when(rate.getFavorite()).thenReturn(true);
		when(rate.getDate()).thenReturn(Instant.now());
		when(rate.getValue()).thenReturn(new BigDecimal("128.23"));
		when(rate.getFromcur()).thenReturn(from);
		when(rate.getTocur()).thenReturn(to);
		
		when(rateService.getAllLatestWithFavorites(eq(1))).thenReturn(List.of(rate));

        // WHEN
        mvc.perform(get("/api/rate/latest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].favorite").value(true))
        .andExpect(jsonPath("$[0].date").value(rate.getDate().toEpochMilli()))
        .andExpect(jsonPath("$[0].value").value(rate.getValue()))
        .andExpect(jsonPath("$[0].fromcur.code").value(from.getCode()))
        .andExpect(jsonPath("$[0].fromcur.name").value(from.getName()))
        .andExpect(jsonPath("$[0].fromcur.countryFlag").value(from.getCountryFlag()))
        .andExpect(jsonPath("$[0].tocur.code").value(to.getCode()))
        .andExpect(jsonPath("$[0].tocur.name").value(to.getName()))
        .andExpect(jsonPath("$[0].tocur.countryFlag").value(to.getCountryFlag()));
    }
	
	@Test
	public void testFindAllLatest_anonymous() throws Exception {
		// GIVEN
		FavoriteRate rate = mock(FavoriteRate.class);
		when(rateService.getAllLatestWithFavorites(isNull())).thenReturn(List.of(rate));

		// WHEN
		mvc.perform(get("/api/rate/latest"))
        .andExpect(status().isOk())
		.andExpect(jsonPath("$[0].favorite").value(false));
	}
}
