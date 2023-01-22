package fr.ymanvieu.trading.webapp.config;

import java.time.Duration;


import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties("jwt")
@Getter
@Setter
@Valid
public class JwtProperties {

	@NotBlank
	private String header;
	@NotBlank
	private String secret;
	
	/**
	 * Access token expiration duration
	 */
	@NotNull
	private Duration expiration;
	
	/**
	 * Refresh token expiration duration
	 */
	@NotNull
	private Duration refreshExpiration;
}
