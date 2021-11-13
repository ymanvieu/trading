package fr.ymanvieu.trading.webapp.oauth2;

import fr.ymanvieu.trading.common.user.UserProvider;
import lombok.Data;

@Data
public class SignUpRequest {

	private String providerUserId;
	private String displayName;
	private String email;
	private UserProvider userProvider;

	public SignUpRequest(String providerUserId, String displayName, String email, UserProvider userProvider) {
		this.providerUserId = providerUserId;
		this.displayName = displayName;
		this.email = email;
		this.userProvider = userProvider;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String providerUserID;
		private String displayName;
		private String email;
		private UserProvider userProvider;

		public Builder addProviderUserID(final String userID) {
			this.providerUserID = userID;
			return this;
		}

		public Builder addDisplayName(final String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder addEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder addSocialProvider(final UserProvider userProvider) {
			this.userProvider = userProvider;
			return this;
		}

		public SignUpRequest build() {
			return new SignUpRequest(providerUserID, displayName, email, userProvider);
		}
	}
}
