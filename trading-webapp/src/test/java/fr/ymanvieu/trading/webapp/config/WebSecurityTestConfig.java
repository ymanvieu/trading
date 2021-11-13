package fr.ymanvieu.trading.webapp.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.oauth2.CustomOAuth2UserService;
import fr.ymanvieu.trading.webapp.oauth2.CustomOidcUserService;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationFailureHandler;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationSuccessHandler;

@Configuration
@Import(WebSecurityConfig.class)
@MockBean({
    JwtTokenUtil.class,
    JdbcUserDetailsManager.class,
    CustomOAuth2UserService.class,
    CustomOidcUserService.class,
    OAuth2AuthenticationSuccessHandler.class,
    OAuth2AuthenticationFailureHandler.class
})
public class WebSecurityTestConfig {

}
