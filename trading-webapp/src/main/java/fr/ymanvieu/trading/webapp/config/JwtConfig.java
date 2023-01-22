package fr.ymanvieu.trading.webapp.config;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.Base64;

@Configuration
public class JwtConfig {

    @Autowired
    private JwtProperties jwtProperties;

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
            .withSecretKey(new SecretKeySpec(Base64.from(jwtProperties.getSecret()).decode(), JwsAlgorithms.HS512))
            .macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new OctetSequenceKey
            .Builder(new SecretKeySpec(Base64.from(jwtProperties.getSecret()).decode(), JwsAlgorithms.HS512))
            .algorithm(JWSAlgorithm.HS512).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
