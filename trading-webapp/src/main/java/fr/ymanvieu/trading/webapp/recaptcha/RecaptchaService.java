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
package fr.ymanvieu.trading.webapp.recaptcha;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ymanvieu.trading.webapp.config.RecaptchaProperties;
import fr.ymanvieu.trading.webapp.recaptcha.exception.RecaptchaException;
import lombok.ToString;

@ConditionalOnBean(RecaptchaProperties.class)
@Service
public class RecaptchaService {

	private static final Logger log = LoggerFactory.getLogger(RecaptchaService.class);

	@ToString(includeFieldNames = true)
	private static class RecaptchaResponse {
		@JsonProperty("success")
		private boolean success;
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;
	}

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private RecaptchaProperties recaptchaProperties;
	
	public boolean isValidRecaptcha(String recaptchaResponse, HttpServletRequest httpServletRequest) {
		if(isNullOrEmpty(recaptchaResponse)) {
			throw new RecaptchaException("recaptchaResponse is null");
		}
		
		return isResponseValid(getRemoteIp(httpServletRequest), recaptchaResponse);
	}

	private boolean isResponseValid(String remoteIp, String response) {
		log.debug("Validating captcha response for remoteIp={}, response={}", remoteIp, response);

		RecaptchaResponse recaptchaResponse;

		try {
			recaptchaResponse = restTemplate.postForEntity(recaptchaProperties.getUrl(), createBody(recaptchaProperties.getSecretKey(), remoteIp, response), 
					RecaptchaResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RecaptchaException("Recaptcha API exception", e);
		}

		if (recaptchaResponse.success) {
			return true;
		}

		log.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);

		return false;
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

	private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secret);
		form.add("remoteip", remoteIp);
		form.add("response", response);
		return form;
	}

}
