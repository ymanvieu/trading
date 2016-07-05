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

import fr.ymanvieu.trading.controller.Response;
import fr.ymanvieu.trading.exception.BusinessException;
import fr.ymanvieu.trading.portofolio.Order;
import fr.ymanvieu.trading.portofolio.OrderInfo;
import fr.ymanvieu.trading.portofolio.OrderService;

@Controller
@RequestMapping("/portofolio")
@PreAuthorize("isAuthenticated()")
public class PortofolioController {

	private static final Logger LOG = LoggerFactory.getLogger(PortofolioController.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "portofolio";
	}

	@RequestMapping(path = "/order", method = RequestMethod.GET)
	public String order(Principal p, Model model, String selected, Float quantity,
			@RequestHeader(required = false, value = "X-Requested-With") String requestedWith) {

		LOG.debug("selected code: {}", selected);

		OrderInfo info = orderService.getInfo(p.getName(), selected, (quantity == null ? 0 : quantity));

		model.addAttribute("orderInfo", info);

		return isAjaxRequest(requestedWith) ? "orders :: result" : "orders";
	}

	public static boolean isAjaxRequest(String requestedWith) {
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	@RequestMapping(path = "/buy", method = RequestMethod.POST)
	public String buy(Principal p, Locale l, RedirectAttributes redirectAttributes, @RequestParam String code, @RequestParam int quantity) {

		Response response = new Response();
		String returnedPage = "redirect:/portofolio";

		try {
			Order order = orderService.buy(p.getName(), code, quantity);
			response.setMessage(toMessage(order, l));
			response.setMessageTitle(messageSource.getMessage("success", null, l));
		} catch (BusinessException e) {
			response.setErrorMessage(messageSource.getMessage(e.getKey(), e.getArgs(), l));
			LOG.warn("{}: {}", e.getClass().getSimpleName(), messageSource.getMessage(e.getKey(), e.getArgs(), null));

			returnedPage = "redirect:order";
		}

		redirectAttributes.addFlashAttribute("response", response);

		return returnedPage;
	}

	@RequestMapping(path = "/sell", method = RequestMethod.POST)
	public String sell(Principal p, Locale l, RedirectAttributes redirectAttributes, @RequestParam String code, @RequestParam int quantity) {

		Response response = new Response();
		String returnedPage = "redirect:/portofolio";

		try {
			Order order = orderService.sell(p.getName(), code, quantity);
			response.setMessage(toMessage(order, l));
			response.setMessageTitle(messageSource.getMessage("success", null, l));
		} catch (BusinessException e) {
			response.setErrorMessage(messageSource.getMessage(e.getKey(), e.getArgs(), l));
			LOG.warn("{}: {}", e.getClass().getSimpleName(), messageSource.getMessage(e.getKey(), e.getArgs(), null));

			returnedPage = "redirect:order";
		}

		redirectAttributes.addFlashAttribute("response", response);

		return returnedPage;
	}

	private String toMessage(Order o, Locale locale) {
		Object[] args = { o.getQuantity(), o.getFrom().getCode(), o.getFrom().getName(), o.getValue(), o.getTo().getCode(), o.getTo().getName() };
		return messageSource.getMessage("order.success", args, locale);
	}
}