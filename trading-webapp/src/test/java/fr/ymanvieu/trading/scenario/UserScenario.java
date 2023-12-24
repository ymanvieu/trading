package fr.ymanvieu.trading.scenario;

import org.junit.jupiter.api.Test;

import fr.ymanvieu.trading.scenario.framework.Scenario;
import fr.ymanvieu.trading.scenario.framework.given.LocalUser;
import fr.ymanvieu.trading.scenario.framework.then.LoginUserVerification;
import fr.ymanvieu.trading.scenario.framework.then.SignupLocalUserVerification;
import fr.ymanvieu.trading.scenario.framework.when.LoginUser;
import fr.ymanvieu.trading.scenario.framework.when.SignupLocalUser;

public class UserScenario extends Scenario {

    @Test
    void signup_local() {
        when(new SignupLocalUser().login("user").password("password").recaptchaResponse("nope"));
        verify(new SignupLocalUserVerification().login("user").authorities("ROLE_USER"));
    }

    @Test
    void signup_local_userAlreadyExist() {
        given(new LocalUser().login("user"));

        when(new SignupLocalUser().login("user").password("password").recaptchaResponse("nope"));
        verify(new SignupLocalUserVerification().error().userAlreadyExist());
    }

    @Test
    void login() {
        var user = given(new LocalUser());

        when(new LoginUser().login(user.login()).password(user.password()));
        verify(new LoginUserVerification().login(user.login()).authorities("ROLE_USER"));
    }
}
