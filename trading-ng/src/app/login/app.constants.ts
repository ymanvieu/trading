export class AppConstants {
    static OAUTH2_URL =  `${location.origin}/api/oauth2/authorization/`;
    static REDIRECT_URL = `?redirect_uri=${location.origin}/login`;
    public static GOOGLE_AUTH_URL = AppConstants.OAUTH2_URL + "google" + AppConstants.REDIRECT_URL;
    public static GITHUB_AUTH_URL = AppConstants.OAUTH2_URL + "github" + AppConstants.REDIRECT_URL;
}
