package eu.fbk.mIDAssistant.IDMP.DB;

public class Amazon implements IDMP {
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
        return discoverURL = "no";
    }

    @Override
    public String AuthorizationURL() {
        return authorizationURL = "https://na.account.amazon.com/ap/oa";
    }

    @Override
    public String TokenURL() {
        return tokenURL = "https://api.amazon.com/auth/o2/token"; }

    @Override
    public String UserInfoURL() {
        return "";
    }

    @Override
    public String DevDomain() {
        return "no";
    }
}
