package fr.ymanvieu.trading.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// Custom JWT based security filter
			.csrf().disable();

		http.headers().frameOptions().sameOrigin(); // h2-console

			// https://docs.spring.io/spring-security/site/docs/5.1.0.RELEASE/reference/htmlsingle/#oauth2login-advanced-login-page
		http.oauth2Login()
			.authorizationEndpoint()
			.baseUri("/api/oauth2/authorization")
			.authorizationRequestRepository(cookieAuthorizationRequestRepository())
			.and()
			.redirectionEndpoint()
			.baseUri("/api/login/oauth2/code/*")
			.and()
			.userInfoEndpoint()
			.oidcUserService(customOidcUserService)
			.userService(customOAuth2UserService)
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler)
		.and()
		.oauth2ResourceServer()
			.jwt()
			.jwtAuthenticationConverter(jwtAuthenticationConverter())
			.and()
			.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
			.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());

		http.authorizeHttpRequests(authorize -> authorize
			.requestMatchers("/api/rate/**", "/api/refresh", "/api/auth", "/api/signup").permitAll()
			.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
			.requestMatchers("/stomp/**").permitAll()

			// Since spring security 6.0, we must explicit default paths (otherwise implicitly Deny access)
			// https://github.com/spring-projects/spring-security/issues/11967
			.anyRequest().authenticated()
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
