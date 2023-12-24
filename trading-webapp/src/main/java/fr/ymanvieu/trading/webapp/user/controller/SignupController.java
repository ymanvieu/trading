package fr.ymanvieu.trading.webapp.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.webapp.config.RecaptchaProperties;
import fr.ymanvieu.trading.webapp.jwt.JwtAuthenticationResponse;
import fr.ymanvieu.trading.webapp.jwt.JwtTokenUtil;
import fr.ymanvieu.trading.webapp.recaptcha.RecaptchaService;
import fr.ymanvieu.trading.webapp.recaptcha.exception.RecaptchaException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private RecaptchaService recaptchaService;

    @Autowired(required = false)
    private RecaptchaProperties recaptchaProperties;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping
    public ResponseEntity<JwtAuthenticationResponse> signup(@Valid @RequestBody SignupForm form, HttpServletRequest httpServletRequest) {

        if (recaptchaProperties != null && recaptchaProperties.isEnabled()) {
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
