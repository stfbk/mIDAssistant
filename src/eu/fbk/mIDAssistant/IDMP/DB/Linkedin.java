package eu.fbk.mIDAssistant.IDMP.DB;

public class Linkedin implements IDMP {
    String solution = "";
    String redirection ="";
    String pkce = "";
    String clientSecret = "";
    String discoverURL = "";
    String authorizationURL ="";
    String tokenURL = "";
    @Override
    public String Solution() {
        return solution = "AD";
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
        return discoverURL = "no";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "https://www.linkedin.com/oauth/v2/authorization";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "https://www.linkedin.com/oauth/v2/accessToken";

    }

    @Override
    public String UserInfoURL() {
        return "";
    }

    @Override
    public String DevDomain() {
        return "no";
    }
}
