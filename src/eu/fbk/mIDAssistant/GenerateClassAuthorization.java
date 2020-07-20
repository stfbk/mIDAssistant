package eu.fbk.mIDAssistant;

public class GenerateClassAuthorization {

    private static GenerateClassAuthorization INSTANCE;
    private GenerateClassAuthorization(){
        idmName = "";
    }
    public static GenerateClassAuthorization getInstance () {
        if (INSTANCE == null){
            INSTANCE = new GenerateClassAuthorization();
        }
        return INSTANCE;
    }
    private String classNameAuthz = "AuthStateManager";
    private String classNameConfz = "Configuration";
    private String classNameConz = "ConnectionBuilderForTesting";
    private String classNameTokenz = "TokenActivity";
    private String scheme;
    private String host;
    private String path;
    private String customURL;
    private String appSecret;
    private String scope;
    private String appID;
    private String btnName;
    private String devDomain;
    private String resourceEnd;
    private String idmName;
    private boolean hasCustom =false;
    private boolean hasAppLink= false;
    private boolean hasRedirect= false;
    private boolean hasAppID, hasBtnName, hasDevDomain, hasSecret = false;

    public String getClassNameAuth() {
        return classNameAuthz;
    }

    public String getClassNameConfiguration() {
        return classNameConfz;
    }

    public String getClassNameConnectionBuilderForTesting() {
        return classNameConz;
    }

    public String getClassNameToken() {
        return classNameTokenz;
    }

    public String getScheme(){
        return scheme;
    }

    public String getHost(){
        return host;
    }

    public String getPath(){
        return path;
    }

    public String getCustomURL() {return customURL;}

    public String getAppSecret() {return appSecret;}

    public String getScope() {return scope;}

    public String getAppID() {return appID;}

    public String getBtnName() {return btnName;}

    public String getDevDomain(){return devDomain;}

    public String getResourceEnd() {return resourceEnd;}

    public String getIdmName() {return idmName;}

    public void setHost(String host){this.host=host;}

    public void setScheme(String scheme){this.scheme=scheme;}

    public void setPath(String path){this.path=path;}

    public void setCustomURL(String customURL){this.customURL=customURL;}

    public void setScope(String scope){this.scope=scope;}

    public void setAppID(String appID){this.appID=appID;}

    public void setBtnName(String btnName){this.btnName=btnName;}

    public void setDevDomain(String devDomain){this.devDomain=devDomain;}

    public void setAppSecret(String appSecret){this.appSecret=appSecret;}

    public void setIdmName (String idmName){this.idmName = idmName;}

    public void setResourceEnd (String resourceEnd) {this.resourceEnd = resourceEnd;}

    void setHasCustom(boolean hasCustom){
        this.hasCustom = hasCustom;
    }

    void setHasSecret(boolean hasSecret) {this.hasSecret = hasSecret;}

    void setHasAppLink(boolean hasAppLink){
        this.hasAppLink = hasAppLink;
    }

     boolean isHasCustom(){
        return hasCustom;
    }

     boolean isHasSecret() {return hasSecret;}

     boolean isHasAppLink(){
        return hasAppLink;
    }

    boolean isHasTest(){
        return true;
    }

    public boolean isHasRedirect(){
        if (isHasCustom() || isHasAppLink())
            hasRedirect=true;
        return hasRedirect;
    }


}
