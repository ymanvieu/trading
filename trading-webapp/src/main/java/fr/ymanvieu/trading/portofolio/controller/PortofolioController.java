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
package fr.ymanvieu.trading.portofolio.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.ymanvieu.trading.controller.BusinessExceptionHandler;
import fr.ymanvieu.trading.controller.Response;
import fr.ymanvieu.trading.exception.BusinessException;
import fr.ymanvieu.trading.portofolio.Order;
import fr.ymanvieu.trading.portofolio.OrderInfo;
import fr.ymanvieu.trading.portofolio.PortofolioService;
import fr.ymanvieu.trading.portofolio.dto.PortofolioMapper;
import fr.ymanvieu.trading.symbol.SymbolException;
import fr.ymanvieu.trading.symbol.dto.SymbolMapper;
import fr.ymanvieu.trading.symbol.entity.SymbolEntity;

@Controller
@RequestMapping("/portofolio")
@PreAuthorize("isAuthenticated()")
public class PortofolioController extends BusinessExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(PortofolioController.class);

	private enum Type {
		BUY,
		SELL
	}

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private PortofolioService portofolioService;

	@RequestMapping(path = "", method = RequestMethod.GET)
	public String index(Principal p, Model model, String selected, Float quantity,
			@RequestHeader(required = false, value = "X-Requested-With") String requestedWith) throws SymbolException {

		log.debug("selected code: {}, quantity: {}", selected, quantity);

		model.addAttribute("portofolio", PortofolioMapper.MAPPER.toPortofolioDto(portofolioService.getPortofolio(p.getName())));

		if (isAjaxRequest(requestedWith)) {
			OrderInfo info = portofolioService.getInfo(p.getName(), selected, (quantity == null ? 0 : quantity));

			model.addAttribute("orderInfo", PortofolioMapper.MAPPER.toOrderInfoDto(info));
		}

		List<SymbolEntity> availableSymbols = portofolioService.getAvailableSymbols(p.getName());

		model.addAttribute("availableSymbols", SymbolMapper.MAPPER.toDto(availableSymbols));

		return isAjaxRequest(requestedWith) ? "portofolio :: result" : "portofolio";
	}

	public static boolean isAjaxRequest(String requestedWith) {
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	@RequestMapping(path = "", method = RequestMethod.POST)
	public String order(Principal p, Locale l, RedirectAttributes redirectAttributes, @RequestParam Type type,
			@RequestParam String code, @RequestParam int quantity) throws BusinessException {

		Response response = new Response();

		Order order = null;

		switch (type) {
			case BUY:
				order = portofolioService.buy(p.getName(), code, quantity);
			break;
			case SELL:
				order = portofolioService.sell(p.getName(), code, quantity);
			break;
			default:
		}

		response.setMessage(toMessage(order, l));
		response.setMessageTitle(messageSource.getMessage("success", null, l));

		redirectAttributes.addFlashAttribute("response", response);

		return "redirect:/portofolio";
	}

	private String toMessage(Order o, Locale locale) {
		Object[] args = { o.getQuantity(), o.getFrom().getCode(), o.getFrom().getName(), o.getValue(), o.getTo().getCode(), o.getTo().getName() };
		return messageSource.getMessage("order.success", args, locale);
	}
}