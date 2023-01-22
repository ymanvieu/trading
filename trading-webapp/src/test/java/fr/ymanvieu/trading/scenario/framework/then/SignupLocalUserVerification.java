package fr.ymanvieu.trading.scenario.framework.then;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.when.SignupLocalUser;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;
import lombok.experimental.Accessors;

public class SignupLocalUserVerification extends AbstractJwtAuthenticationResponseVerification {

    @Override
    protected JwtAuthenticationResponse result(ScenarioContext ctx) throws Exception {
        var action = ctx.lastAction(SignupLocalUser.class);

        action.result()
            .andExpect(status().isOk());

        return action.parseResult();
    }
}
