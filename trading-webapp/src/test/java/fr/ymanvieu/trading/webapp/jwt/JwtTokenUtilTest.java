/**
 * Copyright (C) 2019 Yoann Manvieu
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

import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;

@ExtendWith(SpringExtension.class)
@Import( {JwtTokenUtil.class, TradingWebAppConfig.class})
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
		String username = jwtTokenUtil.getClaimFromToken(token, c -> c.get(JwtTokenUtil.CLAIM_KEY_USERNAME, String.class));
		assertThat(username).isEqualTo("username");

		assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isBetween(start, Instant.now());
		assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isBetween(start.plus(expiration), Instant.now().plus(expiration));
	

		String[] roles = jwtTokenUtil.getClaimFromToken(token, c -> c.get(JwtTokenUtil.CLAIM_KEY_ROLES, String.class)).split(JwtTokenUtil.ROLES_CLAIM_DELIMITER);
		
		assertThat(roles).containsExactlyInAnyOrder("ADMIN", "USER");
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
