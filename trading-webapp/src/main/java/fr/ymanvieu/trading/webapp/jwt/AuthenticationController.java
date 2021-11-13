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
package fr.ymanvieu.trading.webapp.jwt;

import java.util.Map;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.user.UserService;
import io.jsonwebtoken.JwtException;

/**
 * https://github.com/szerhusenBC/jwt-spring-security-demo/tree/master/src/main/java/org/zerhusen
 */
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/auth")
    public ResponseEntity<JwtAuthenticationResponse> createAuthenticationToken(@RequestBody @Valid JwtAuthenticationRequest authenticationRequest)
    		throws AuthenticationException {

        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        final String accessToken = jwtTokenUtil.generateToken(authentication.getName(), authenticationRequest.getUsername(), authentication.getAuthorities());
        final String refreshToken = jwtTokenUtil.generateRefreshToken(authentication.getName());

        // Return the token
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshAndGetAuthenticationToken(@RequestBody Map<String, String> body) {
        String token = body.get("refreshToken");

        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("refreshToken is missing");
        }

        var userId = Integer.valueOf(jwtTokenUtil.getSubjectFromToken(token));
        final UserDetails userDetails = userService.getUser(userId);
        
        if(!jwtTokenUtil.validateToken(token, userDetails)) {
        	throw new JwtException("wrong username or expired refresh token");
        }

        final String accessToken = jwtTokenUtil.generateToken(Integer.toString(userId), userService.getUsername(userId), userDetails.getAuthorities());
        String refreshedToken = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshedToken));

    }

}
