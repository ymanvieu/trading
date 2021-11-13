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

import static java.util.stream.Collectors.joining;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import fr.ymanvieu.trading.webapp.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenUtil {

	static final String CLAIM_KEY_ROLES = "roles";
	public static final String CLAIM_KEY_USERNAME = "username";
	static final String ROLES_CLAIM_DELIMITER = ",";

    @Autowired
    private JwtProperties jwtProperties;

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Instant getIssuedAtDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getIssuedAt).getTime());
    }

    public Instant getExpirationDateFromToken(String token) {
        return Instant.ofEpochMilli(getClaimFromToken(token, Claims::getExpiration).getTime());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Instant expiration = getExpirationDateFromToken(token);
        return expiration.isBefore(Instant.now());
    }

    public String generateToken(String subject, String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_ROLES, grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(joining(ROLES_CLAIM_DELIMITER)));
        claims.put(CLAIM_KEY_USERNAME, username);

        log.info("generateToken()");
        
        return doGenerateToken(subject, claims);
    }

    private String doGenerateToken(String subject, Map<String, Object> claims) {
        final Instant createdDate = Instant.now();
        final Instant expirationDate = createdDate.plus(jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(createdDate))
                .setExpiration(Date.from(expirationDate))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String subject) {
    	final Instant createdDate = Instant.now();
    	final Instant expirationDate = createdDate.plus(jwtProperties.getRefreshExpiration());
    	
    	log.info("generateRefreshToken()");
        
        return Jwts.builder()
        		.setSubject(subject)
                .setIssuedAt(Date.from(createdDate))
                .setExpiration(Date.from(expirationDate))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())), SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getSubjectFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    

    public Collection<? extends GrantedAuthority> getGrantedAuthoritiesFromToken(String authToken) {
    	return getClaimFromToken(authToken, t -> {
    		return Stream.of(t.get(JwtTokenUtil.CLAIM_KEY_ROLES, String.class)
    				.split(JwtTokenUtil.ROLES_CLAIM_DELIMITER))
    				.map(SimpleGrantedAuthority::new)
    				.collect(Collectors.toList());
    	});
    }
}