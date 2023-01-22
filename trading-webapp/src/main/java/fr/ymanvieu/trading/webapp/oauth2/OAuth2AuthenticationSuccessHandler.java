package fr.ymanvieu.trading.webapp.oauth2;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserService userService;

	@Autowired
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).map(
			Cookie::getValue);

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

		// authentication.getName() => userId
		var displayName = userService.getUsername(Integer.valueOf(authentication.getName()));

		String token = jwtTokenUtil.generateToken(authentication.getName(), displayName, authentication.getAuthorities());
		String refreshToken = jwtTokenUtil.generateRefreshToken(authentication.getName());

		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("accessToken", token)
			.queryParam("refreshToken", refreshToken)
			.build()
			.toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}
}
