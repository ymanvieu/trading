package fr.ymanvieu.trading.webapp.jwt;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.user.UserService;
import jakarta.validation.Valid;

// https://github.com/szerhusenBC/jwt-spring-security-demo/tree/master/src/main/java/org/zerhusen
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
    public ResponseEntity<JwtAuthenticationResponse> createAuthenticationToken(@RequestBody @Valid AuthenticationRequest authenticationRequest)
    		throws AuthenticationException {

        // Perform the security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        String accessToken = jwtTokenUtil.generateToken(authentication.getName(), authenticationRequest.getUsername(), authentication.getAuthorities());
        String refreshToken = jwtTokenUtil.generateRefreshToken(authentication.getName());

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
        UserDetails userDetails = userService.getUser(userId);
        
        if(!jwtTokenUtil.validateToken(token, userDetails)) {
        	throw new JwtException("wrong username or expired refresh token");
        }

        String accessToken = jwtTokenUtil.generateToken(Integer.toString(userId), userService.getUsername(userId), userDetails.getAuthorities());
        String refreshedToken = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok(new JwtAuthenticationResponse(accessToken, refreshedToken));

    }

}
