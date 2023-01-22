package fr.ymanvieu.trading.webapp.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		log.error(exception.getMessage(), exception);

		String targetUrl = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue).orElse(("/"));

		targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("error", exception.getMessage()).build().toUriString();

		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
