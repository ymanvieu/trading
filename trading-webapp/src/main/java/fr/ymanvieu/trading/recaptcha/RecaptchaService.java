package fr.ymanvieu.trading.recaptcha;

import java.util.Collection;

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

import fr.ymanvieu.trading.config.RecaptchaProperties;
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

	public boolean isResponseValid(String remoteIp, String response) {
		log.debug("Validating captcha response for remoteIp={}, response={}", remoteIp, response);

		RecaptchaResponse recaptchaResponse;

		try {
			recaptchaResponse = restTemplate.postForEntity(recaptchaProperties.getUrl(), createBody(recaptchaProperties.getSecretKey(), remoteIp, response), 
					RecaptchaResponse.class).getBody();
		} catch (RestClientException e) {
			throw new RecaptchaServiceException("Recaptcha API exception", e);
		}

		if (recaptchaResponse.success) {
			return true;
		}

		log.debug("Unsuccessful recaptchaResponse={}", recaptchaResponse);

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
