package fr.ymanvieu.trading.webapp.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.ymanvieu.trading.common.exception.BusinessException;
import fr.ymanvieu.trading.common.user.UserAlreadyExistsException;
import fr.ymanvieu.trading.webapp.recaptcha.exception.RecaptchaException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionRestHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleBusinessException(BusinessException e, Locale l) {
		ResponseDTO response = new ResponseDTO();
		
		String message = messageSource.getMessage(e.getKey(), e.getArgs(), e.getKey(), l);
		
		return ResponseEntity.badRequest().body(response.setMessage(message).setArgs(e.getArgs()));
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler
	public void handleExpiredJwtException(JwtException ex) {
		log.warn("", ex);
	}
	
	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleRecaptchaException(RecaptchaException ex) {
		ResponseDTO response = new ResponseDTO();

		response.setMessage(ex.getMessage());

		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale l) {
		ResponseDTO response = new ResponseDTO();
		
		response.setMessage(messageSource.getMessage("user.error.login.exists", new Object[] {ex.getLogin()}, l));
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleBadCredentialsExceptionException(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO().setMessage(ex.getMessage()));
	}
}
