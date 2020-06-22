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
package fr.ymanvieu.trading.webapp.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.ymanvieu.trading.common.exception.BusinessException;
import fr.ymanvieu.trading.webapp.recaptcha.exception.RecaptchaException;
import fr.ymanvieu.trading.webapp.user.exception.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class ExceptionRestHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler
	public ResponseEntity<Response> handleBusinessException(BusinessException e, Locale l) {
		Response response = new Response();
		
		String message = messageSource.getMessage(e.getKey(), e.getArgs(), e.getKey(), l);
		
		return ResponseEntity.badRequest().body(response.setMessage(message).setArgs(e.getArgs()));
	}
	
	@ExceptionHandler
	public ResponseEntity<Response> handleExpiredJwtException(JwtException ex) {
		Response response = new Response();

		response.setMessage(ex.getMessage());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
	
	@ExceptionHandler
	public ResponseEntity<Response> handleRecaptchaException(RecaptchaException ex) {
		Response response = new Response();

		response.setMessage(ex.getMessage());

		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler
	public ResponseEntity<Response> handleUserAlreadyExistsException(UserAlreadyExistsException ex, Locale l) {
		Response response = new Response();
		
		response.setMessage(messageSource.getMessage("user.error.login.exists", new Object[] {ex.getLogin()}, l));
		
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}