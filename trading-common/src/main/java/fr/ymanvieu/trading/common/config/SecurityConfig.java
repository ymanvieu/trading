package fr.ymanvieu.trading.common.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

@EnableGlobalAuthentication
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource ds) {
        var jdbcUserDetailsManager = new JdbcUserDetailsManager(ds);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select id,password,enabled from users where username = ? and provider = 'local'");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select user_id,authority from authorities where user_id = ?::int");
        return jdbcUserDetailsManager;
    }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
