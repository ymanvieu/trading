package fr.ymanvieu.trading.common.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

// https://docs.spring.io/spring-security/site/docs/current/reference/html5/#enableglobalmethodsecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager(AuthenticationManagerBuilder auth, DataSource ds, PasswordEncoder pwEncoder) throws Exception {
        JdbcUserDetailsManager jdbcUserDetailsManager = auth
            .jdbcAuthentication()
            .usersByUsernameQuery("select id,password,enabled from users where username = ? and provider = 'local'")
            .authoritiesByUsernameQuery("select user_id,authority from authorities where user_id = ?::int")
            .dataSource(ds)
            .passwordEncoder(pwEncoder)
            .getUserDetailsService();
        jdbcUserDetailsManager.setAuthenticationManager(authenticationManager());
        return jdbcUserDetailsManager;
    }
    
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
