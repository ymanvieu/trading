package fr.ymanvieu.trading.webapp.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.test.config.RestTestConfig;
import fr.ymanvieu.trading.test.config.WebSecurityTestConfig;
import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;

@WebMvcTest
@Import({AuthenticationController.class})
@ContextConfiguration(classes = {TradingWebAppConfig.class, WebSecurityTestConfig.class, RestTestConfig.class})
class AuthenticationControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MockMvc mvc;

    @Test
    void createAuthenticationToken_emptyPassword() throws Exception {
        // WHEN
        mvc.perform(post("/api/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"user\", \"password\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAuthenticationToken_emptyUsername() throws Exception {
        // WHEN
        mvc.perform(post("/api/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"\", \"password\": \"pass\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void refreshAndGetAuthenticationToken_missingToken() throws Exception {

        mvc.perform(post("/api/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isBadRequest());
    }
    @Test
    void refreshAndGetAuthenticationToken_malformedToken() throws Exception {
        when(jwtTokenUtil.getSubjectFromToken(any())).thenReturn("1");

        mvc.perform(post("/api/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\": \"dde\"}"))
            .andExpect(status().isUnauthorized());
    }
}
