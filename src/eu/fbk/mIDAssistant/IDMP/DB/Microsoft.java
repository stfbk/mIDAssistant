package eu.fbk.mIDAssistant.IDMP.DB;

public class Microsoft implements IDMP {

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
        return redirection = "HTTPS";
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
        return discoverURL = "/.well-known/openid-configuration";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "/oauth2/v2.0/authorize";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "/oauth2/v2.0/token";
    }

    @Override
    public String UserInfoURL() {
        return userInfoURL = "yes";
    }

    @Override
    public String DevDomain() {
        return devDomain = "yes";
    }
}
