package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.SignupLocalUser;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;

public class SignupLocalUserVerification extends AbstractJwtAuthenticationResponseVerification {

    private HttpStatus status = HttpStatus.OK;
    private String errorCode;

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var action = ctx.lastAction(SignupLocalUser.class);

        action.result()
            .andExpect(MockMvcResultMatchers.status().is(status.value()));

        if (status == HttpStatus.OK) {
            super.internalVerify(ctx);
        } else {
            var error = action.parseError();
            assertThat(error.getMessage()).isEqualTo(errorCode);
        }
    }

    @Override
    protected JwtAuthenticationResponse result(ScenarioContext ctx) throws Exception {
        return ctx.lastAction(SignupLocalUser.class).parseResult();
    }

    public SignupLocalUserError error() {
        return new SignupLocalUserError();
    }

    public class SignupLocalUserError {

        public SignupLocalUserVerification userAlreadyExist() {
            status = HttpStatus.CONFLICT;
            errorCode = "user.error.username-already-exists";
            return SignupLocalUserVerification.this;
        }
    }
}
