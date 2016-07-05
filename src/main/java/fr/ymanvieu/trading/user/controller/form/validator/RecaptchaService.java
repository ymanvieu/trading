package fr.ymanvieu.trading.user.controller.form.validator;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ymanvieu.trading.user.controller.RecaptchaServiceException;

@ConditionalOnWebApplication
@Service
public class RecaptchaService {

	private static class RecaptchaResponse {
		@JsonProperty("success")
		private boolean success;
		@JsonProperty("error-codes")
		private Collection<String> errorCodes;

		@Override
		public String toString() {
			return toStringHelper(this).add("success", success).add("errorCodes", errorCodes).toString();
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(RecaptchaService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${recaptcha.url}")
	private String recaptchaUrl;

	@Value("${recaptcha.secret-key}")
	private String recaptchaSecretKey;

	public boolean isResponseValid(String remoteIp, String response) {
		LOG.debug("Validating captcha response for remoteIp={}, response={}", remoteIp, response);

		RecaptchaResponse recaptchaResponse;

		try {
			recaptchaResponse = restTemplate.postForEntity(recaptchaUrl, createBody(recaptchaSecretKey, remoteIp, response), 
					RecaptchaResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RecaptchaServiceException("Recaptcha API exception", e);
		}

		if (recaptchaResponse.success) {
			return true;
		}

		LOG.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);

		return false;
	}

	private MultiValueMap<String, String> createBody(String secret, String remoteIp, String response) {
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("secret", secret);
		form.add("remoteip", remoteIp);
		form.add("response", response);
		return form;
	}

}
