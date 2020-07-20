package eu.fbk.mIDAssistant.IDMP.DB;

public class Yahoo implements IDMP {

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
        return pkce = "no";
    }

    @Override
    public String ClientSecret() {
        return clientSecret = "yes";
    }

    @Override
    public String DiscoverURL() {
        return discoverURL = "https://api.login.yahoo.com/.well-known/openid-configuration";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "https://api.login.yahoo.com/oauth2/request_auth";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "https://api.login.yahoo.com/oauth2/get_token";
    }

    @Override
    public String UserInfoURL() {
        return userInfoURL = "https://api.login.yahoo.com/openid/v1/userinfo";
    }

    @Override
    public String DevDomain() {
        return devDomain="no";
    }
}
