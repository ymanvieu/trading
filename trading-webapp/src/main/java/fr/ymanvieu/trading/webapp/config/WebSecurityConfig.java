/**
 * Copyright (C) 2015 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import fr.ymanvieu.trading.common.config.SecurityConfig;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationTokenFilter;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.oauth2.CustomOAuth2UserService;
import fr.ymanvieu.trading.webapp.oauth2.CustomOidcUserService;
import fr.ymanvieu.trading.webapp.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AccessTokenResponseConverterWithDefaults;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationFailureHandler;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Order
@Import(SecurityConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	@Autowired
	private CustomOidcUserService customOidcUserService;

	@Autowired
	private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	@Autowired
	private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		// Custom JWT based security filter
		.csrf().disable()

		// https://docs.spring.io/spring-security/site/docs/5.1.0.RELEASE/reference/htmlsingle/#oauth2login-advanced-login-page
		.oauth2Login()
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
			.tokenEndpoint()
				.accessTokenResponseClient(authorizationCodeTokenResponseClient())
				.and()
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler);

		http.addFilterBefore(new JwtAuthenticationTokenFilter(jwtTokenUtil, tokenHeader), UsernamePasswordAuthenticationFilter.class);
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

	
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/api/rate/.**", "/api/refresh", "/api/auth");
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
		OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
		tokenResponseHttpMessageConverter.setTokenResponseConverter(new OAuth2AccessTokenResponseConverterWithDefaults());
		RestTemplate restTemplate = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), tokenResponseHttpMessageConverter));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		DefaultAuthorizationCodeTokenResponseClient tokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		tokenResponseClient.setRestOperations(restTemplate);
		return tokenResponseClient;
	}
}