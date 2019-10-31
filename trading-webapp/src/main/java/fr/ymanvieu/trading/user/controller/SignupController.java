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
package fr.ymanvieu.trading.user.controller;

import static java.util.Arrays.asList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.jwt.JwtAuthenticationResponse;
import fr.ymanvieu.trading.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.portofolio.PortofolioService;
import fr.ymanvieu.trading.recaptcha.RecaptchaService;
import fr.ymanvieu.trading.recaptcha.exception.RecaptchaException;
import fr.ymanvieu.trading.symbol.util.CurrencyUtils;
import fr.ymanvieu.trading.user.exception.UserAlreadyExistsException;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

	@Autowired
	private PortofolioService portofolioService;

	@Autowired(required = false)
	private RecaptchaService recaptchaService;
	
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	// TODO in service
	@Transactional
	@PostMapping
	public ResponseEntity<JwtAuthenticationResponse> signup(@Valid @RequestBody SignupForm form, HttpServletRequest httpServletRequest) {

		if(recaptchaService != null) {
			if(!recaptchaService.isValidRecaptcha(form.getRecaptchaResponse(), httpServletRequest)) {
				throw new RecaptchaException("Invalid recaptchaResponse");
			}
		}

		if(userDetailsManager.userExists(form.getLogin())) {
			throw new UserAlreadyExistsException(form.getLogin());
		}
		
		UserDetails userDetails = new User(form.getLogin(), passwordEncoder.encode(form.getPassword()), asList(new SimpleGrantedAuthority(Role.USER.name())));
		
		userDetailsManager.createUser(userDetails);
		
		portofolioService.createPortofolio(form.getLogin(), CurrencyUtils.EUR, 100_000);
		
		String accessToken = jwtTokenUtil.generateToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(accessToken);

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
	}
}