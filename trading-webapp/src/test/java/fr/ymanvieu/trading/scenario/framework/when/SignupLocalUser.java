package fr.ymanvieu.trading.scenario.framework.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.webapp.controller.ResponseDTO;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;
import fr.ymanvieu.trading.webapp.user.controller.SignupForm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@ToString(exclude = "result")
public class SignupLocalUser extends AbstractWhenAction {

    private String login;
    private String password;
    private String recaptchaResponse;

    @Getter
    private ResultActions result;

    @Override
    protected void internalExecute(ScenarioContext ctx) throws Exception {
        Preconditions.checkNotNull(login);
        Preconditions.checkNotNull(password);

        var signupForm = new SignupForm().setLogin(login).setPassword(password).setRecaptchaResponse(recaptchaResponse);

        result = ctx.performHttpRequest(post("/api/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper().writeValueAsBytes(signupForm)));
    }

    public JwtAuthenticationResponse parseResult() {
        try {
            return objectMapper().readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ResponseDTO parseError() {
        try {
            return objectMapper().readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
