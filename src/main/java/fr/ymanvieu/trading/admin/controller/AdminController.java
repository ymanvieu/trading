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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.base.Strings;

import fr.ymanvieu.trading.admin.AdminService;
import fr.ymanvieu.trading.admin.SymbolInfo;
import fr.ymanvieu.trading.controller.Response;
import fr.ymanvieu.trading.provider.LookupInfo;
import fr.ymanvieu.trading.provider.LookupService;
import fr.ymanvieu.trading.provider.Pair;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.symbol.SymbolException;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

	private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PairService pairService;

	@Autowired
	private AdminService adminService;

	@Autowired
	private LookupService clService;

	@RequestMapping(method = RequestMethod.GET)
	public String symbols(Model model, String code) throws IOException {

		final List<Pair> existingSymbols;
		final List<LookupInfo> availableSymbols;

		if (Strings.isNullOrEmpty(code)) {
			existingSymbols = pairService.getAll();
			availableSymbols = new ArrayList<>();
		} else {
			existingSymbols = pairService.getAllWithSymbolOrNameContaining(code);
			availableSymbols = clService.search(code);
			removeDuplicates(availableSymbols, existingSymbols);
		}

		model.addAttribute("existingSymbols", existingSymbols);
		model.addAttribute("availableSymbols", availableSymbols);

		return "admin";
	}

	private void removeDuplicates(List<LookupInfo> availableSymbols, List<Pair> existingSymbols) {
		Iterator<LookupInfo> it = availableSymbols.iterator();

		while (it.hasNext()) {
			LookupInfo cl = it.next();

			for (Pair pair : existingSymbols) {
				if (pair.getSymbol().equals(cl.getCode())) {
					it.remove();
					break;
				}
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String add(RedirectAttributes redirectAttributes, Locale l, @RequestParam String code, @RequestParam String provider) throws IOException {
		Response response = new Response();

		try {
			SymbolInfo si = adminService.add(code, provider);

			Object[] args = { si.getName(), si.getQuote().getCode(), si.getQuote().getPrice(), si.getQuote().getCurrency(), si.getQuote().getTime() };
			response.setMessage(messageSource.getMessage("symbols.success.add", args, l));
			response.setMessageTitle(messageSource.getMessage("success", null, l));

			if (!si.isHistoryFound()) {
				response.setWarningMessage(messageSource.getMessage("symbols.warning.no_historical_data", new Object[] { si.getCode() }, l));
			}
		} catch (SymbolException e) {
			LOG.warn("{}: {}", e.getClass().getSimpleName(), messageSource.getMessage(e.getKey(), e.getArgs(), null));
			response.setErrorMessage(messageSource.getMessage(e.getKey(), e.getArgs(), l));
		}

		redirectAttributes.addFlashAttribute("response", response);

		return "redirect:admin";
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(RedirectAttributes redirectAttributes, Locale l, @RequestParam String code) {
		Response response = new Response();

		try {
			adminService.delete(code);

			response.setMessage(messageSource.getMessage("symbols.success.delete", new Object[] { code }, l));
			response.setMessageTitle(messageSource.getMessage("success", null, l));
		} catch (SymbolException e) {
			LOG.warn("{}: {}", e.getClass().getSimpleName(), messageSource.getMessage(e.getKey(), e.getArgs(), null));
			response.setErrorMessage(messageSource.getMessage(e.getKey(), e.getArgs(), l));
		}

		redirectAttributes.addFlashAttribute("response", response);

		return "redirect:admin";
	}
}