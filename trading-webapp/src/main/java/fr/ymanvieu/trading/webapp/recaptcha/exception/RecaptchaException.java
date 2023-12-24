package fr.ymanvieu.trading.webapp.recaptcha.exception;

public class RecaptchaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecaptchaException(String message) {
		super(message);
	}
	
	public RecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

}
