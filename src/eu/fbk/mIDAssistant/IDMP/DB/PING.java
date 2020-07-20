package eu.fbk.mIDAssistant.IDMP.DB;

public class PING implements IDMP {

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
        return solution = "SSO";
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
        return discoverURL = "/as/.well-known/openid-configuration";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "yes";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "yes";
    }

    @Override
    public String UserInfoURL() {
        return userInfoURL = "yes";
    }

    @Override
    public String DevDomain () { return devDomain = "yes"; }
}
