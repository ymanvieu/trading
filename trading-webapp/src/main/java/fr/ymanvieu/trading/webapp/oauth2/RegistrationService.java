package fr.ymanvieu.trading.webapp.oauth2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import fr.ymanvieu.trading.common.user.UserAlreadyExistsException;
import fr.ymanvieu.trading.common.user.UserProvider;
import fr.ymanvieu.trading.common.user.UserService;
import fr.ymanvieu.trading.common.user.entity.UserEntity;
import fr.ymanvieu.trading.common.user.repository.UserRepository;
import fr.ymanvieu.trading.webapp.oauth2.exception.OAuth2AuthenticationProcessingException;
import fr.ymanvieu.trading.webapp.oauth2.user.LocalUser;
import fr.ymanvieu.trading.webapp.oauth2.user.OAuth2UserInfo;
import fr.ymanvieu.trading.webapp.oauth2.user.OAuth2UserInfoFactory;

@Service
@Transactional
public class RegistrationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserService userService;

	public User registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistsException {
		var providerUserId = signUpRequest.getProviderUserId();
		var provider = signUpRequest.getUserProvider();

		if (userRepository.existsByProviderUserIdAndProvider(providerUserId, provider.getProviderType())) {
			throw new UserAlreadyExistsException("User with providerUserId: " + providerUserId + " and Provider: " + provider + " already exists");
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new UserAlreadyExistsException("User with email: " + signUpRequest.getEmail() + " already exists");
		}

		return userService.createSocialUser(signUpRequest.getDisplayName(),
			signUpRequest.getUserProvider(), signUpRequest.getProviderUserId(), signUpRequest.getEmail());
	}

	public LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
		if (!StringUtils.hasText(oAuth2UserInfo.getName())) {
			throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
		} else if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		SignUpRequest signUpRequest = toUserRegistrationObject(registrationId, oAuth2UserInfo);
		UserEntity user = userRepository.findByEmail(oAuth2UserInfo.getEmail());
		UserDetails userDetails;
		if (user != null) {
			if (!user.getProvider().equals(registrationId)) {
				throw new OAuth2AuthenticationProcessingException(
						"Looks like you're signed up with " + user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
			}

			userDetails = userService.updateSocialUser(signUpRequest.getEmail(), signUpRequest.getDisplayName());
		} else {
			userDetails = registerNewUser(signUpRequest);
		}

		return LocalUser.create(userDetails, attributes, idToken, userInfo);
	}

	private SignUpRequest toUserRegistrationObject(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
		return SignUpRequest.getBuilder()
			.addProviderUserID(oAuth2UserInfo.getId())
			.addDisplayName(oAuth2UserInfo.getName())
			.addEmail(oAuth2UserInfo.getEmail())
			.addSocialProvider(UserProvider.toUserProvider(registrationId)).build();
	}
}
