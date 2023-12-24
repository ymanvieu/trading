package fr.ymanvieu.trading.webapp.config;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import fr.ymanvieu.trading.common.config.SecurityConfig;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.oauth2.CustomOAuth2UserService;
import fr.ymanvieu.trading.webapp.oauth2.CustomOidcUserService;
import fr.ymanvieu.trading.webapp.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationFailureHandler;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
// https://docs.spring.io/spring-security/reference/5.7.3/servlet/authorization/method-security.html#_enablemethodsecurity
@EnableMethodSecurity
@EnableWebSecurity
@Import({SecurityConfig.class, JwtConfig.class})
public class WebSecurityConfig {

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	private CustomOidcUserService customOidcUserService;

	@Autowired
	private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Autowired
	private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// Custom JWT based security filter
			.csrf(AbstractHttpConfigurer::disable);

		http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin)); // h2-console

		// https://docs.spring.io/spring-security/site/docs/5.1.0.RELEASE/reference/htmlsingle/#oauth2login-advanced-login-page
		http.oauth2Login(oauth2Login -> oauth2Login
				.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
					.baseUri("/api/oauth2/authorization")
					.authorizationRequestRepository(cookieAuthorizationRequestRepository()))

			.redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint
				.baseUri("/api/login/oauth2/code/*"))

			.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
				.oidcUserService(customOidcUserService)
				.userService(customOAuth2UserService))
				.successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler))

		.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
			.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
			.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
			.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()));

		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers(mvc.pattern("/api/rate/**"), mvc.pattern("/api/refresh"), mvc.pattern("/api/auth"), mvc.pattern("/api/signup")).permitAll()
			.requestMatchers(mvc.pattern("/api/**")).authenticated()
			.requestMatchers(mvc.pattern("/stomp/**")).permitAll()
			.requestMatchers(antMatcher("/h2-console/**")).permitAll()
		);

		return http.build();
	}


	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		// create a custom JWT converter to map the roles from the token as granted authorities
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(JwtTokenUtil.CLAIM_KEY_AUTHORITIES); // default is: scope, scp
		jwtGrantedAuthoritiesConverter.setAuthorityPrefix(""); // default is: SCOPE_

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	/*
	 * By default, Spring OAuth2 uses
	 * HttpSessionOAuth2AuthorizationRequestRepository to save the authorization
	 * request. But, since our service is stateless, we can't save it in the
	 * session. We'll save the request in a Base64 encoded cookie instead.
	 */
	@Bean
	public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}
}
