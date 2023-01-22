package fr.ymanvieu.trading.webapp.config;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@ConditionalOnProperty(prefix = "recaptcha", name = "enabled", havingValue = "true")
@ConfigurationProperties("recaptcha")
@Getter
@Setter
@Valid
public class RecaptchaProperties {

	private boolean enabled;

	@NotNull
	private URI url;
	@NotBlank
	private String secretKey;
}
