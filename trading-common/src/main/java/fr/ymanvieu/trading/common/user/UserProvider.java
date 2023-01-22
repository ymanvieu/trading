package fr.ymanvieu.trading.common.user;

public enum UserProvider {

    GOOGLE("google"),
    GITHUB("github"),
    LOCAL("local");

    private final String providerType;

    public String getProviderType() {
        return providerType;
    }

    UserProvider(final String providerType) {
        this.providerType = providerType;
    }

    public static UserProvider toUserProvider(String providerType) {
        for (UserProvider userProvider : UserProvider.values()) {
            if (userProvider.getProviderType().equals(providerType)) {
                return userProvider;
            }
        }

        throw new IllegalArgumentException("unknown providerType: " + providerType);
    }
}
