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
package fr.ymanvieu.trading.user.controller;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.ymanvieu.trading.config.RecaptchaProperties;
import fr.ymanvieu.trading.controller.Response;
import fr.ymanvieu.trading.portofolio.Portofolio;
import fr.ymanvieu.trading.portofolio.PortofolioService;
import fr.ymanvieu.trading.recaptcha.RecaptchaFormValidator;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.user.UserException;
import fr.ymanvieu.trading.user.UserService;
import fr.ymanvieu.trading.user.controller.form.UserCreateForm;
import fr.ymanvieu.trading.user.controller.form.validator.UserCreateFormPasswordValidator;
import fr.ymanvieu.trading.user.entity.UserEntity;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserCreateFormPasswordValidator passwordValidator;

	@Autowired
	private RecaptchaFormValidator recaptchaFormValidator;

	@Autowired
	private UserService userService;

	@Autowired
	private PortofolioService portofolioService;

	@Autowired(required = false)
	private RecaptchaProperties recaptchaProperties;

	@InitBinder("form")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(passwordValidator);
		
		if(isRecaptchaEnabled()) {
			binder.addValidators(recaptchaFormValidator);
		}
	}

	@ModelAttribute("isRecaptchaEnabled")
	public boolean isRecaptchaEnabled() {
		return recaptchaProperties != null && recaptchaProperties.isEnabled();
	}

	@ModelAttribute("recaptchaSiteKey")
	public String getRecaptchaSiteKey() {
		return (isRecaptchaEnabled()) ? recaptchaProperties.getSiteKey() : null;
	}

	@RequestMapping(path = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/**
	 * @param form
	 */
	@RequestMapping(path = "/signup", method = RequestMethod.GET)
	public String show(@ModelAttribute("form") UserCreateForm form) {
		return "signup";
	}

	@Transactional
	@RequestMapping(path = "/signup", method = RequestMethod.POST)
	public String signup(@ModelAttribute("form") @Valid UserCreateForm form, BindingResult result, RedirectAttributes redirectAttributes, Locale l) {

		if (result.hasErrors()) {
			return "signup";
		}

		UserEntity ue;
		Portofolio portofolio;

		try {
			ue = userService.createUser(form.getLogin(), form.getPassword1());
			portofolio = portofolioService.createPortofolio(form.getLogin(), CurrencyUtils.EUR, 100_000);
		} catch (UserException e) {
			result.rejectValue("login", "user.error.login.exists");
			return "signup";
		}

		Response resp = new Response();
		resp.setMessageTitle(messageSource.getMessage("success", null, l));
		Object[] args = { ue.getLogin(), portofolio.getBaseCurrency().getQuantity(), portofolio.getBaseCurrency().getSymbol().getCode() };
		resp.setMessage(messageSource.getMessage("user.create.success", args, l));
		redirectAttributes.addFlashAttribute("response", resp);

		return "redirect:/";
	}
}