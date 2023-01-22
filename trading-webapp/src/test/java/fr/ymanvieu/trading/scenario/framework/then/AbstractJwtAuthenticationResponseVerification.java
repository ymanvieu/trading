package fr.ymanvieu.trading.scenario.framework.then;

import static org.assertj.core.api.Assertions.assertThat;

import fr.ymanvieu.trading.scenario.framework.ScenarioContext;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class AbstractJwtAuthenticationResponseVerification extends AbstractThenVerification {

    @Setter
    private String login;
    private String[] authorities;
    @Getter
    private Long userId;

    public AbstractJwtAuthenticationResponseVerification authorities(String... authorities) {
        this.authorities = authorities;
        return this;
    }

    protected abstract JwtAuthenticationResponse result(ScenarioContext ctx) throws Exception;

    @Override
    protected void internalVerify(ScenarioContext ctx) throws Exception {
        var result = result(ctx);

        var jwtTokenUtil = ctx.getBean(JwtTokenUtil.class);

        assertThat(result.getAccessToken()).isNotEmpty();
        assertThat(result.getRefreshToken()).isNotEmpty();

        if(login != null) {
            assertThat(jwtTokenUtil.getUsernameFromToken(result.getAccessToken())).isEqualTo(login);
        }

        if (authorities != null) {
            assertThat(jwtTokenUtil.getAuthoritiesFromToken(result.getAccessToken())).containsExactlyInAnyOrder(authorities);
        }

        assertThat(jwtTokenUtil.getSubjectFromToken(result.getAccessToken())).isEqualTo(jwtTokenUtil.getSubjectFromToken(result.getRefreshToken()));

        userId = Long.parseLong(jwtTokenUtil.getSubjectFromToken(result.getAccessToken()));
    }
}
