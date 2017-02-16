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
package fr.ymanvieu.trading.config;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Order
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired(required = false)
	private PersistentTokenRepository persistentTokenRepository;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.formLogin().defaultSuccessUrl("/portofolio") //
				.loginPage("/user/login") //
				.loginProcessingUrl("/user/login") //
				.and() //
				.logout().logoutSuccessUrl("/");

		RememberMeConfigurer<HttpSecurity> rememberMeConfigurer = http.rememberMe();

		if (persistentTokenRepository != null) {
			UserDetailsService uds = http.getSharedObject(UserDetailsService.class);
			String key = UUID.randomUUID().toString();

			CustomPersistentTokenBasedRememberMeServices customRememberMeServices = new CustomPersistentTokenBasedRememberMeServices(key, uds, persistentTokenRepository);
			// 2 months validity
			customRememberMeServices.setTokenValiditySeconds(5184000);

			rememberMeConfigurer.tokenRepository(persistentTokenRepository) //
					.key(customRememberMeServices.getKey()) //
					.rememberMeServices(customRememberMeServices);
		}
	}

	@Profile("!dev")
	@Bean
	public PersistentTokenRepository persistentTokenRepository(DataSource ds) {
		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		tokenRepositoryImpl.setDataSource(ds);
		return tokenRepositoryImpl;
	}

	/**
	 * Disable Cookie theft check. <br>
	 * Avoid CookieTheftException when some browsers (chrome/firefox) quickly reload multiple times a page. <br>
	 * (sending the same remember-me cookie multiple times) <br>
	 * https://github.com/spring-projects/spring-security/issues/3079
	 */
	public class CustomPersistentTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {
		public CustomPersistentTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
			super(key, userDetailsService, tokenRepository);
		}

		@Override
		protected String generateTokenData() {
			// Return a constant value for the token value to avoid CookieTheftExceptions.
			return "n59D4i1dnaBaPyh5LkZldQ==";
		}
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, DataSource ds, PasswordEncoder pwEncoder) throws Exception {
		auth.jdbcAuthentication().dataSource(ds).passwordEncoder(pwEncoder) //
				.usersByUsernameQuery("select login,password,true from users where login=?") //
				.authoritiesByUsernameQuery("select login,role from users where login=?");
	}
}