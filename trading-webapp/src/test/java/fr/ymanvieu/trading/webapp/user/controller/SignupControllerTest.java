/**
 * Copyright (C) 2020 Yoann Manvieu
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
package fr.ymanvieu.trading.webapp.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.List;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import fr.ymanvieu.trading.common.user.Role;
import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;
import fr.ymanvieu.trading.webapp.config.WebSecurityConfig;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.oauth2.CustomOAuth2UserService;
import fr.ymanvieu.trading.webapp.oauth2.CustomOidcUserService;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationFailureHandler;
import fr.ymanvieu.trading.webapp.oauth2.OAuth2AuthenticationSuccessHandler;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@Import({ SignupController.class, WebSecurityConfig.class, JwtTokenUtil.class })
@ContextConfiguration(classes = { TradingWebAppConfig.class })
@MockBean({
    JdbcUserDetailsManager.class,
    CustomOAuth2UserService.class,
    CustomOidcUserService.class,
    OAuth2AuthenticationSuccessHandler.class,
    OAuth2AuthenticationFailureHandler.class,
})
public class SignupControllerTest {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void testSignup() throws Exception {
        // GIVEN
        var login = "user";
        var userId = "1";
        var grantedAuthorities = List.of(new SimpleGrantedAuthority(Role.USER.name()));
        User ud = new User(userId, "", grantedAuthorities);

        when(userService.createLocalUser(eq(login), eq("password"))).thenReturn(ud);

        // WHEN
        mvc.perform(post("/api/signup")
                .content("{\"login\": \"user\", \"password\": \"password\", \"recaptchaResponse\": \"frf\"}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("accessToken").value(hasJwtTokenSubject(userId)))
        .andExpect(jsonPath("accessToken").value(hasJwtTokenGrantedAuthorities(grantedAuthorities)))
        .andExpect(jsonPath("accessToken").value(hasJwtTokenUsername(login)))
        .andExpect(jsonPath("refreshToken").value(hasJwtTokenSubject(userId)));
    }
    
    private Matcher<String> hasJwtTokenSubject(String expectedUserId) {
        return new CustomMatcher<>(expectedUserId) {
            @Override
            public boolean matches(Object obj) {
                return expectedUserId.equals(jwtTokenUtil.getSubjectFromToken((String)obj));
            }
            
            @Override
            public void describeMismatch(Object item, Description description) {
                super.describeMismatch(jwtTokenUtil.getSubjectFromToken((String)item), description);
            }
        };
    }

    private Matcher<String> hasJwtTokenUsername(String expectedUsername) {
        return new CustomMatcher<>(expectedUsername) {
            @Override
            public boolean matches(Object obj) {
                String username = jwtTokenUtil.getClaimFromToken((String) obj, c -> c.get(JwtTokenUtil.CLAIM_KEY_USERNAME, String.class));
                return expectedUsername.equals(username);
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                super.describeMismatch(jwtTokenUtil.getSubjectFromToken((String)item), description);
            }
        };
    }

    private Matcher<Collection<? extends GrantedAuthority>> hasJwtTokenGrantedAuthorities(Collection<? extends GrantedAuthority> expectedGrantedAuthorities) {
        return new CustomMatcher<>(expectedGrantedAuthorities.toString()) {
            @Override
            public boolean matches(Object obj) {
                return expectedGrantedAuthorities.equals(jwtTokenUtil.getGrantedAuthoritiesFromToken((String)obj));
            }
            
            @Override
            public void describeMismatch(Object item, Description description) {
                super.describeMismatch(jwtTokenUtil.getGrantedAuthoritiesFromToken((String)item), description);
            }
        };
    }
}