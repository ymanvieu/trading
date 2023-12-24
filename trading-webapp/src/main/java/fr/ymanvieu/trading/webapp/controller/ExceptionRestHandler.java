package fr.ymanvieu.trading.webapp.controller;

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

	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleBusinessException(BusinessException e) {
		ResponseDTO response = new ResponseDTO();

		return ResponseEntity.badRequest().body(response.setMessage(e.getKey()).setArgs(e.getArgs()));
	}

	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler
	public void handleExpiredJwtException(JwtException ex) {
		log.warn("", ex);
	}
	
	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleRecaptchaException(RecaptchaException ex) {
		log.warn(ex.getMessage(), ex);
		return ResponseEntity.badRequest().body(new ResponseDTO().setMessage(ex.getMessage()));
	}
	
	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		ResponseDTO response = new ResponseDTO();
		
		response.setMessage(ex.getKey());
		response.setArgs(ex.getArgs());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<ResponseDTO> handleBadCredentialsExceptionException(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO().setMessage(ex.getMessage()));
	}
}
