package fr.ymanvieu.trading.webapp.recaptcha;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public abstract class RecaptchaForm {
	
	protected String recaptchaResponse;
}
