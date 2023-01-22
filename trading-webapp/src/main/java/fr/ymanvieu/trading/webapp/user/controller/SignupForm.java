package fr.ymanvieu.trading.webapp.user.controller;

import fr.ymanvieu.trading.webapp.recaptcha.RecaptchaForm;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString(exclude = { "password"})
@Accessors(chain = true)
public class SignupForm extends RecaptchaForm {

	@NotEmpty
	@Size(min = 3, max = 64)
	private String login;

	@NotEmpty
	@Size(min = 8, max = 64)
	private String password;
}
