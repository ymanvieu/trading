package fr.ymanvieu.trading.webapp.oauth2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.user.UserProvider;
import fr.ymanvieu.trading.webapp.oauth2.exception.OAuth2AuthenticationProcessingException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private RegistrationService userService;

	@Autowired
	private Environment env;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		try {
			Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
			String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
			if (provider.equals(UserProvider.GITHUB.getProviderType())) {
				populateEmailAddressFromGithub(oAuth2UserRequest, attributes);
			}
			return userService.processUserRegistration(provider, attributes, null, null);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new OAuth2AuthenticationProcessingException(ex.getMessage(), ex);
		}
	}

	@SuppressWarnings({"unchecked" })
	public void populateEmailAddressFromGithub(OAuth2UserRequest oAuth2UserRequest, Map<String, Object> attributes) throws OAuth2AuthenticationException {
		String emailEndpointUri = env.getProperty("github.email-address-uri");
		Assert.notNull(emailEndpointUri, "Github email address end point required");
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();


		headers.add(HttpHeaders.AUTHORIZATION, "token " + oAuth2UserRequest.getAccessToken().getTokenValue());
		HttpEntity<?> entity = new HttpEntity<>("", headers);
		ResponseEntity<Object> response = restTemplate.exchange(emailEndpointUri, HttpMethod.GET, entity, Object.class);
		List<?> list = (List<?>) response.getBody();
		Map<String, String> map = (Map<String, String>) list.get(0);
		String email = map.get("email");
		//todo test if attribute "verified" true

		attributes.put("email", email);
	}
}