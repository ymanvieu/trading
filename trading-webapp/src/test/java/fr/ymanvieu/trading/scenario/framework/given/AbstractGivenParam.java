package fr.ymanvieu.trading.scenario.framework.given;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.TestSecurityContextHolder;
import com.github.ymanvieu.test.scenario.given.GivenParam;

import fr.ymanvieu.trading.common.user.Role;
import fr.ymanvieu.trading.scenario.framework.ScenarioContext;

public abstract class AbstractGivenParam extends GivenParam<ScenarioContext> {

    private SecurityContext existingSecurityContext;

    @Override
    public final void create(ScenarioContext scenarioContext) {
        try {
            super.create(scenarioContext);
        } finally {
            restoreUserInContext();
        }
    }


    protected void useAdmin() {
        existingSecurityContext = TestSecurityContextHolder.getContext();

        UserDetails admin = User.withUsername("0").authorities(Role.ADMIN).password("").build();
        var authorities = admin.getAuthorities();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(admin, null, authorities);

        TestSecurityContextHolder.setAuthentication(auth);
    }

    protected void restoreUserInContext() {
        if (existingSecurityContext != null) {
            TestSecurityContextHolder.setContext(existingSecurityContext);
            existingSecurityContext = null;
        }
    }
}
