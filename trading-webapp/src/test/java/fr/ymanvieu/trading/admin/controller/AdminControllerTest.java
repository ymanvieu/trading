/**
 * Copyright (C) 2019 Yoann Manvieu
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
package fr.ymanvieu.trading.admin.controller;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import fr.ymanvieu.trading.admin.AdminService;
import fr.ymanvieu.trading.admin.SearchResult;
import fr.ymanvieu.trading.admin.SymbolInfo;
import fr.ymanvieu.trading.config.TradingWebAppConfig;
import fr.ymanvieu.trading.config.WebSecurityTestConfig;
import fr.ymanvieu.trading.rate.Rate;

@RunWith(SpringRunner.class)
@WebMvcTest
@Import(AdminController.class)
@WithMockUser(authorities = "ADMIN")
@ContextConfiguration(classes = { TradingWebAppConfig.class, WebSecurityTestConfig.class })
public class AdminControllerTest {

	@MockBean
	private AdminService adminService;

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void testSymbols() throws Exception {
		// GIVEN
		SearchResult result = new SearchResult(asList(), asList());

		when(adminService.search(eq("volks"))).thenReturn(result);

		// WHEN
		mvc.perform(get("/api/admin")
				.param("code", "volks"))
		.andExpect(status().is2xxSuccessful());
	}
	
	@WithMockUser
	@Test
	public void testSymbolsNotAdmin() throws Exception {

		// WHEN
		mvc.perform(get("/api/admin")
				.param("code", "volks"))
		.andExpect(status().isForbidden());
	}

	@Test
	public void testAdd() throws Exception {
		// GIVEN
		SymbolInfo si = new SymbolInfo("code", "name", true, new Rate("code", null, null));

		when(adminService.add(eq("UBI.PA"), eq("YAHOO"))).thenReturn(si);

		// WHEN
		mvc.perform(post("/api/admin/YAHOO/UBI.PA"))
		.andExpect(status().is2xxSuccessful());
	}
	
	@WithMockUser
	@Test
	public void testAddNotAdmin() throws Exception {
		// WHEN
		mvc.perform(post("/api/admin/YAHOO/UBI.PA"))
		.andExpect(status().isForbidden());
	}

	@Test
	public void testDelete() throws Exception {

		// WHEN
		mvc.perform(delete("/api/admin/YAHOO/UBI.PA"))
		.andExpect(status().is2xxSuccessful());
		
		verify(adminService).delete(eq("UBI.PA"), eq("YAHOO"));
	}
	
	@WithMockUser
	@Test
	public void testDeleteNotAdmin() throws Exception {
		
		// WHEN
		mvc.perform(delete("/api/admin/YAHOO/UBI.PA"))
		.andExpect(status().isForbidden());
	}

}
