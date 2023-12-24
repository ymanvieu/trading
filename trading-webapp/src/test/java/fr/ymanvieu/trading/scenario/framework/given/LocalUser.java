package fr.ymanvieu.trading.scenario.framework.given;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.scenario.framework.then.AbstractJwtAuthenticationResponseVerification;
import fr.ymanvieu.trading.scenario.framework.then.SignupLocalUserVerification;
import fr.ymanvieu.trading.scenario.framework.when.SignupLocalUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Accessors(fluent = true)
public class LocalUser extends AbstractGivenParam {

    @Getter
    @Setter
    private String login = "user" + nextId();
    @Getter
    @Setter
    private String password = "password";
    @Getter
    private Long userId;


    @Override
    protected void internalCreate(ScenarioContext ctx) {
        ctx.getDSL()
            .when(new SignupLocalUser()
                .login(login)
                .password(password)
                .recaptchaResponse("random"));
        userId = ctx.getDSL().verify(new SignupLocalUserVerification().login(login).authorities("ROLE_USER")).userId();
    }
}
