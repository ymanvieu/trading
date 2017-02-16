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
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.ymanvieu.trading.admin.AdminService;
import fr.ymanvieu.trading.admin.SymbolInfo;
import fr.ymanvieu.trading.controller.BusinessExceptionHandler;
import fr.ymanvieu.trading.controller.Response;
import fr.ymanvieu.trading.provider.PairService;
import fr.ymanvieu.trading.provider.PairsResult;
import fr.ymanvieu.trading.provider.ProviderException;
import fr.ymanvieu.trading.provider.dto.PairMapper;
import fr.ymanvieu.trading.provider.dto.PairDTO;
import fr.ymanvieu.trading.symbol.SymbolException;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController extends BusinessExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PairService pairService;

	@Autowired
	private AdminService adminService;

	@RequestMapping(method = RequestMethod.GET)
	public String symbols(Model model, String code) throws IOException {

		PairsResult result = pairService.search(code);

		List<PairDTO> existingPairs = PairMapper.MAPPER.toPairDto(result.getExistingPairs());
		
		model.addAttribute("existingSymbols", existingPairs);
		model.addAttribute("availableSymbols", result.getAvailableSymbols());

		return "admin";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String add(RedirectAttributes redirectAttributes, Locale l, @RequestParam String code, @RequestParam String provider)
			throws SymbolException, ProviderException, IOException {

		Response response = new Response();

		SymbolInfo si = adminService.add(code, provider);

		Object[] args = { si.getName(), si.getCode(), si.getQuote().getPrice(), si.getQuote().getCurrency(), si.getQuote().getTime() };
		response.setMessage(messageSource.getMessage("symbols.success.add", args, l));
		response.setMessageTitle(messageSource.getMessage("success", null, l));

		if (!si.isHistoryFound()) {
			response.setWarningMessage(messageSource.getMessage("symbols.warning.no_historical_data", new Object[] { si.getCode() }, l));
		}

		redirectAttributes.addFlashAttribute("response", response);

		return "redirect:admin";
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public String delete(RedirectAttributes redirectAttributes, Locale l, @RequestParam String code) throws SymbolException {
		Response response = new Response();

		adminService.delete(code);

		response.setMessage(messageSource.getMessage("symbols.success.delete", new Object[] { code }, l));
		response.setMessageTitle(messageSource.getMessage("success", null, l));

		redirectAttributes.addFlashAttribute("response", response);

		return "redirect:admin";
	}
}