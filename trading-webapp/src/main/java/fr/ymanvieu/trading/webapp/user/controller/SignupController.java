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
package fr.ymanvieu.trading.webapp.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.recaptcha.RecaptchaService;
import fr.ymanvieu.trading.webapp.recaptcha.exception.RecaptchaException;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private RecaptchaService recaptchaService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping
    public ResponseEntity<JwtAuthenticationResponse> signup(@Valid @RequestBody SignupForm form, HttpServletRequest httpServletRequest) {

        if (recaptchaService != null) {
            if (!recaptchaService.isValidRecaptcha(form.getRecaptchaResponse(), httpServletRequest)) {
                throw new RecaptchaException("Invalid recaptchaResponse");
            }
        }

        var userDetails = userService.createLocalUser(form.getLogin(), form.getPassword());

        String accessToken = jwtTokenUtil.generateToken(userDetails.getUsername(), form.getLogin(), userDetails.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
    }
}