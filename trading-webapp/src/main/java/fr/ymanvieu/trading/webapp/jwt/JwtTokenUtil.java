package fr.ymanvieu.trading.webapp.jwt;

import static java.util.stream.Collectors.joining;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.ymanvieu.trading.webapp.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenUtil {

	public static final String CLAIM_KEY_AUTHORITIES = "scope";
	public static final String CLAIM_KEY_USERNAME = "username";
	static final String AUTHORITIES_CLAIM_DELIMITER = " ";

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private JwtDecoder decoder;

    public String getSubjectFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    public Instant getIssuedAtDateFromToken(String token) {
        return getAllClaimsFromToken(token).getIssuedAt();
    }

    public Instant getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiresAt();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getClaim(CLAIM_KEY_USERNAME);
    }

    public String[] getAuthoritiesFromToken(String token) {
        String authorities = getAllClaimsFromToken(token).getClaim(CLAIM_KEY_AUTHORITIES);
        return StringUtils.hasText(authorities) ? authorities.split(AUTHORITIES_CLAIM_DELIMITER) : new String[]{};
    }

    public static String getUsernameFromToken(JwtAuthenticationToken token) {
        return token.getToken().getClaimAsString(CLAIM_KEY_USERNAME);
    }

    private Jwt getAllClaimsFromToken(String token) {
        return decoder.decode(token);
    }

    private Boolean isTokenExpired(String token) {
        final Instant expiration = getExpirationDateFromToken(token);
        return expiration.isBefore(Instant.now());
    }

    public String generateToken(String subject, String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_AUTHORITIES, grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(joining(AUTHORITIES_CLAIM_DELIMITER)));
        claims.put(CLAIM_KEY_USERNAME, username);

        log.info("generateToken()");
        
        return doGenerateToken(subject, claims);
    }

    private String doGenerateToken(String subject, Map<String, Object> claims) {
        final Instant createdDate = Instant.now();
        final Instant expirationDate = createdDate.plus(jwtProperties.getExpiration());

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(createdDate)
            .expiresAt(expirationDate)
            .subject(subject)
            .claims((cs) -> cs.putAll(claims))
            .build();
        return this.encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claimsSet)).getTokenValue();
    }

    public String generateRefreshToken(String subject) {
    	final Instant createdDate = Instant.now();
    	final Instant expirationDate = createdDate.plus(jwtProperties.getRefreshExpiration());
    	
    	log.info("generateRefreshToken()");

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(createdDate)
            .expiresAt(expirationDate)
            .subject(subject)
            .build();
        return this.encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claimsSet)).getTokenValue();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getSubjectFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthoritiesFromToken(String authToken) {
        return Stream.of(getAuthoritiesFromToken(authToken))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
