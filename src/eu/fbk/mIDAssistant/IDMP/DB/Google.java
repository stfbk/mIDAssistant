package eu.fbk.mIDAssistant.IDMP.DB;

public class Google implements IDMP {

    String solution = "";
    String redirection ="";
    String pkce = "";
    String clientSecret = "";
    String discoverURL = "";
    String authorizationURL ="";
    String tokenURL = "";
    String userInfoURL ="";
    String devDomain = "";
    @Override
    public String Solution() {
        return solution = "both";
    }

    @Override
    public String Redirection() {
        return redirection = "both";
    }

    @Override
    public String Pkce() {
        return pkce = "yes";
    }

    @Override
    public String ClientSecret() {
        return clientSecret = "no";
    }

    @Override
    public String DiscoverURL() {
        return discoverURL = "https://accounts.google.com/.well-known/openid-configuration";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "https://accounts.google.com/o/oauth2/v2/auth";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "https://oauth2.googleapis.com/token";
    }

    @Override
    public String UserInfoURL() {
        return userInfoURL = "https://openidconnect.googleapis.com/v1/userinfo";
    }

    @Override
    public String DevDomain() {
        return devDomain ="no";
    }
}
