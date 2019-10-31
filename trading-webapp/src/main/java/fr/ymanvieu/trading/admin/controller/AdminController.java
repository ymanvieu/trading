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
package fr.ymanvieu.trading.admin.controller;

import java.io.IOException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.admin.AdminService;
import fr.ymanvieu.trading.admin.SearchResult;
import fr.ymanvieu.trading.admin.SymbolInfo;
import fr.ymanvieu.trading.controller.Response;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AdminService adminService;

	@GetMapping
	public SearchResult symbols(String code) throws IOException {
		return adminService.search(code);
	}

	@PostMapping("/{provider}/{code}")
	public Response add(Locale l, @PathVariable String code, @PathVariable String provider) throws IOException {

		Response response = new Response();

		SymbolInfo si = adminService.add(code, provider);

		Object[] args = { si.getName(), si.getCode(), si.getQuote().getPrice(), si.getQuote().getCurrency(), si.getQuote().getTime() };
		response.setMessage(messageSource.getMessage("symbols.success.add", args, l));
		response.setMessageTitle(messageSource.getMessage("success", null, l));

		if (!si.isHistoryFound()) {
			response.setWarningMessage(messageSource.getMessage("symbols.warning.no_historical_data", new Object[] { si.getCode() }, l));
		}
		
		return response;
	}

	@DeleteMapping("/{provider}/{symbol}")
	public Response delete(Locale l, @PathVariable String symbol, @PathVariable String provider) {
		Response response = new Response();

		adminService.delete(symbol, provider);

		response.setMessage(messageSource.getMessage("symbols.success.delete", new Object[] { symbol }, l));
		response.setMessageTitle(messageSource.getMessage("success", null, l));

		return response;
	}
}