package fr.ymanvieu.trading.webapp.jwt;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import fr.ymanvieu.trading.webapp.config.JwtConfig;
import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;

@ExtendWith(SpringExtension.class)
@Import( {JwtTokenUtil.class, TradingWebAppConfig.class, JwtConfig.class})
public class JwtTokenUtilTest {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Test
	public void testGenerateToken() {
		// GIVEN
		Instant start = Instant.now().minusSeconds(1);
		User userDetails = new User("userId", "", asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));
		Duration expiration = Duration.ofMinutes(30);
		
		// WHEN
		String token = jwtTokenUtil.generateToken(userDetails.getUsername(),"username", userDetails.getAuthorities());


		// THEN
		assertThat(jwtTokenUtil.getSubjectFromToken(token)).isEqualTo("userId");
		String username = jwtTokenUtil.getUsernameFromToken(token);
		assertThat(username).isEqualTo("username");

		assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isBetween(start, Instant.now());
		assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isBetween(start.plus(expiration), Instant.now().plus(expiration));
	

		String[] authorities = jwtTokenUtil.getAuthoritiesFromToken(token);
		
		assertThat(authorities).containsExactlyInAnyOrder("ADMIN", "USER");
	}

	@Test
	public void testGenerateRefreshToken() {
		// GIVEN
		Instant start = Instant.now().minusSeconds(1);
		Duration expiration = Duration.ofDays(90);
		
		// WHEN
		String token = jwtTokenUtil.generateRefreshToken("username");
		
		
		// THEN
		assertThat(jwtTokenUtil.getSubjectFromToken(token)).isEqualTo("username");
		assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isBetween(start, Instant.now());
		assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isBetween(start.plus(expiration), Instant.now().plus(expiration));
	}
}
