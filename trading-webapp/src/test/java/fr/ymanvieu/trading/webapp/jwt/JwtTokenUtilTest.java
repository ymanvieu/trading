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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;

import fr.ymanvieu.trading.webapp.config.TradingWebAppConfig;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;

@RunWith(SpringRunner.class)
@Import( {JwtTokenUtil.class, TradingWebAppConfig.class})
public class JwtTokenUtilTest {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Test
	public void testGenerateToken() throws Exception {
		// GIVEN
		Instant start = Instant.now().minusSeconds(1);
		User userDetails = new User("username", "", asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));
		Duration expiration = Duration.ofMinutes(30);
		
		// WHEN
		String token = jwtTokenUtil.generateToken(userDetails);
		
		
		// THEN
		assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("username");
		assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isBetween(start, Instant.now());
		assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isBetween(start.plus(expiration), Instant.now().plus(expiration));
	

		String[] roles = jwtTokenUtil.getClaimFromToken(token, c -> c.get(JwtTokenUtil.CLAIM_KEY_ROLES, String.class)).split(JwtTokenUtil.ROLES_CLAIM_DELIMITER);
		
		assertThat(roles).containsExactlyInAnyOrder("ADMIN", "USER");	
	}

	@Test
	public void testGenerateRefreshToken() throws Exception {
		// GIVEN
		Instant start = Instant.now().minusSeconds(1);
		Duration expiration = Duration.ofDays(90);
		
		// WHEN
		String token = jwtTokenUtil.generateRefreshToken("username");
		
		
		// THEN
		assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("username");
		assertThat(jwtTokenUtil.getIssuedAtDateFromToken(token)).isBetween(start, Instant.now());
		assertThat(jwtTokenUtil.getExpirationDateFromToken(token)).isBetween(start.plus(expiration), Instant.now().plus(expiration));
	}
}
