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
package fr.ymanvieu.trading.recaptcha;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

/**
 * http://kielczewski.eu/2014/12/spring-boot-security-application/
 * http://kielczewski.eu/2015/07/spring-recaptcha-v2-form-validation/
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RecaptchaFormValidator implements Validator {

	private static final Logger log = LoggerFactory.getLogger(RecaptchaFormValidator.class);

	private static final String ERROR_RECAPTCHA_EMPTY = "recaptcha.error.empty";
	private static final String ERROR_RECAPTCHA_INVALID = "recaptcha.error.invalid";
	private static final String ERROR_RECAPTCHA_UNAVAILABLE = "recaptcha.error.unavailable";

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private RecaptchaService recaptchaService;	

	@Override
	public boolean supports(Class<?> clazz) {
		return RecaptchaForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RecaptchaForm form = (RecaptchaForm) target;

		String recaptchaResponse = form.getRecaptchaResponse();
		try {
			if(isNullOrEmpty(recaptchaResponse)) {
				errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_EMPTY);
			}else if (!recaptchaService.isResponseValid(getRemoteIp(httpServletRequest), recaptchaResponse)) {
				errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_INVALID);
			}
		} catch (RecaptchaServiceException e) {
			log.error("Exception occurred when validating captcha response", e);
			errors.rejectValue("recaptchaResponse", ERROR_RECAPTCHA_UNAVAILABLE);
		}
	}

	private String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
