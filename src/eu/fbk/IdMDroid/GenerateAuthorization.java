package eu.fbk.IdMDroid;

import com.android.tools.idea.gradle.dsl.api.GradleBuildModel;
import com.android.tools.idea.gradle.dsl.api.android.AndroidModel;
import com.android.tools.idea.gradle.dsl.api.dependencies.DependenciesModel;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.json.psi.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import eu.fbk.IdMDroid.IDMP.DB.IDMP;
import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static com.android.tools.idea.gradle.dsl.api.dependencies.CommonConfigurationNames.IMPLEMENTATION;

/**
 * Class GenerateAuthorization is in charge of create the main Authorization code
 * add the intent-filter inside the manifest.xml
 * add the AppAuth classes (duplicate classes)
 * add the json configuration file, button name, and TokenActivity class (by the time we won't add it)
 */

public class GenerateAuthorization extends AnAction {

    private InsertCodeHandlerAuthz insertCodeHandler;
    private InsertCodeHandlerAuthz insertCodeHandlerboth;
    private Project myProject;
    private PsiDirectory psiDirectory;
    private PsiDirectory resourceDirectory;
    private PsiManager psiManager;
    private PsiClass psiclass;
    private boolean both = false;
    @Override
    public void actionPerformed(AnActionEvent e) {
         psiclass = getPsiClassFromContext(e);
        assert psiclass != null;
        myProject = psiclass.getProject();
        e.getPresentation().setEnabled(true);
        GenerateDialogAuthorization dlg = new GenerateDialogAuthorization (psiclass);
        dlg.show();
        PsiShortNamesCache psnc = PsiShortNamesCache.getInstance(myProject);
        String[] names = psnc.getAllClassNames();
        boolean contains = Arrays.asList(names).contains("Configuration");
        if (contains){
            both = true;
        }
        System.out.println(both);
        String idpName = GenerateClassAuthorization.getInstance().getIdmName();
        if ((idpName.equals("Box")  || idpName.equals("DropBox") || idpName.equals("Linkedin")) && (!GenerateClassAuthorization.getInstance().isHasRedirect()) && (GenerateClassAuthorization.getInstance().getAppID().equals("")) && (GenerateClassAuthorization.getInstance().getBtnName().equals("")) && (GenerateClassAuthorization.getInstance().getAppSecret().equals("")))
        {
            if (dlg.isOK()) {
                Messages.showErrorDialog("Please complete the needed information ", "Please, Fill The Mandatory Fields");
            }


        } else if (idpName.equals("Google") && (!GenerateClassAuthorization.getInstance().isHasRedirect()) && (GenerateClassAuthorization.getInstance().getAppID().equals("")) && (GenerateClassAuthorization.getInstance().getBtnName().equals(""))) {
            if (dlg.isOK()) {
                Messages.showErrorDialog("Please complete the needed information ", "Please, Fill The Mandatory Fields");
            }
        } else if (idpName.equals("Microsoft") && (!GenerateClassAuthorization.getInstance().isHasRedirect()) && (GenerateClassAuthorization.getInstance().getAppID().equals("")) && (GenerateClassAuthorization.getInstance().getBtnName().equals("")) && (GenerateClassAuthorization.getInstance().getDevDomain().equals(""))) {
            if (dlg.isOK()) {
                Messages.showErrorDialog("Please complete the needed information ", "Please, Fill The Mandatory Fields");
            }
        } else {
            if (dlg.isOK()) {
                dlg.doValidate();
                if (both){
                    generateComparableboth(psiclass);
                    generateSupportiveClasses();
                    generateJsonConfig();
                    try{
                        inspectBuildGradleboth();
                    }catch (IOException e1){
                        e1.printStackTrace();
                    }
                    inspectJSONFile();
                } else {
                    generateComparable(psiclass);
                    insertCodeHandler = new InsertCodeHandlerAuthz(psiclass);
                    insertCodeHandler.invoke(myProject);
                    generateSupportiveClasses();
                    generateJsonConfig();
                    try {
                        inspectBuildGradle();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    inspectJSONFile();
                }

            }
        }
    }


    /**
     * this is the function for just adding authorization Button
     * @param psiClass
     */
    private void generateComparable(PsiClass psiClass) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {

                generateCompareTo(psiClass);
            }
        }.execute();
    }

    /**
     * this is the function for adding authorization Button at the same time as authentication
     * @param psiClass
     */
    private void generateComparableboth(PsiClass psiClass) {
        new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {

                generateCompareToboth(psiClass);
            }
        }.execute();
    }

    /**
     * this class will generate necessary classes that are used to integrate the AppAuth Library into the project
     * In order to implement this part the file template concept has been used
     */
    private void generateSupportiveClasses() {

        if (both){
            String authStateManagerTemplate = "AuthStateManagerAuthorizationClassTemplate.java";
            String configTemplate = "ConfigurationAuthorizationTemplate.java";
            String authorizationTemplate ="AuthorizationActivityTemplate.java";
            JavaDirectoryService.getInstance().createClass(psiDirectory, "ConfigurationAuthorization", configTemplate, true);
            JavaDirectoryService.getInstance().createClass(psiDirectory, "AuthStateManagerAuthorization", authStateManagerTemplate, true);
           // JavaDirectoryService.getInstance().createClass(psiDirectory, "AuthorizationActivity",authorizationTemplate , true);
           // generateUserInterfaceFileBoth();
            inspectAndroidManifestboth();

        } else {
            String glideTemplate = "AppAuthGlideModuleTemplate.java";
            String configTemplate = "ConfigurationTemplate.java";
            String connectionTemplate = "ConnectionBuilderForTestingTemplate.java";
            String authStateManagerTemplate = "AuthStateManagerClassTemplate.java";
           // String oidchWithRefTokentemplate = "TokenActivityOIDCWithRefTokenTemplate.java";
            JavaDirectoryService.getInstance().createClass(psiDirectory, "AppAuthGlideModule", glideTemplate, true);
            JavaDirectoryService.getInstance().createClass(psiDirectory, GenerateClassAuthorization.getInstance().getClassNameAuth(), authStateManagerTemplate, true);
            JavaDirectoryService.getInstance().createClass(psiDirectory, GenerateClassAuthorization.getInstance().getClassNameConfiguration(),configTemplate , true);
            JavaDirectoryService.getInstance().createClass(psiDirectory, GenerateClassAuthorization.getInstance().getClassNameConnectionBuilderForTesting(), connectionTemplate, true);
            if (GenerateClassAuthorization.getInstance().isHasTest()){
                //JavaDirectoryService.getInstance().createClass(psiDirectory, "TokenActivity",oidchWithRefTokentemplate , true);
               // generateUserInterfaceFile();
                inspectAndroidManifest();
            }
        }

    }


    /**
     * this function is used for the single authorization, it will add the layout file related to the Authorization Activity
     */
    private void generateUserInterfaceFile() {
        psiManager = psiManager.getInstance(myProject);
        String pathToFile = myProject.getBasePath() + "/app/src/main/res/layout/activity_main.xml";
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(pathToFile).getParent();
        FileTemplate fileTemplate = FileTemplateManager.getInstance(myProject).getInternalTemplate("TokenActivity");
        resourceDirectory = psiManager.findDirectory(virtualFile);
        CreateFileFromTemplateAction.createFileFromTemplate("activity_token", fileTemplate, resourceDirectory,null,true);

    }

    /**
     * this function is used to add authorization at the same time as Authentication
     */
    private void generateUserInterfaceFileBoth() {
        psiManager = psiManager.getInstance(myProject);
        String pathToFile = myProject.getBasePath() + "/app/src/main/res/layout/activity_main.xml";
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(pathToFile).getParent();
        FileTemplate fileTemplate = FileTemplateManager.getInstance(myProject).getInternalTemplate("AuthorizationActivity");
        resourceDirectory = psiManager.findDirectory(virtualFile);
        CreateFileFromTemplateAction.createFileFromTemplate("activity_authorization", fileTemplate, resourceDirectory,null,true);

    }

    /**
     * this class will will generate an empty json structure.
     */
    private void generateJsonConfig() {
        psiManager = psiManager.getInstance(myProject);
        String pathToFile = myProject.getBasePath() + "/app/src/main/res/raw/";
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(pathToFile);
        FileTemplate fileTemplate = FileTemplateManager.getInstance(myProject).getInternalTemplate("ConfigJson");
        resourceDirectory = psiManager.findDirectory(virtualFile);
        CreateFileFromTemplateAction.createFileFromTemplate("authorization_config", fileTemplate, resourceDirectory,null,true);

    }

    /**
     * this function is used to add one authorization (single scenario)
     */
    private void inspectAndroidManifest() {
        //String pathToManifest = myProject.getBasePath() + "/app/src/main/AndroidManifest.xml";
        //VirtualFile manifestFile = LocalFileSystem.getInstance().findFileByPath(pathToManifest);
        Module[] modules = ModuleManager.getInstance(myProject).getSortedModules();
        // get all modules of all open projects
        // Module[] modules = getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getSortedModules();
        Module module = modules[modules.length-2];
        AndroidFacet facet=AndroidFacet.getInstance(module);
        Manifest manifest = facet.getManifest();
        if (manifest==null){
            return;
        }
        final XmlTag manifestTag = manifest.getXmlTag();
        if (manifestTag == null){
            return;
        }

        XmlTag applicationTag = manifestTag.findFirstSubTag("application");

        // Add the code in order to support multiple applink addition

        /*XmlTag testTag[] = manifestTag.findSubTags("activity");
        for (XmlTag tag:testTag){
            if (tag.getAttributeValue("android:name").equals("net.openid.appauth.RedirectUriReceiverActivity")){
                // to do
            }
        }*/
        if (applicationTag==null){
            return;
        }

        final PsiFile manifestFile = manifestTag.getContainingFile();
        if (manifestFile == null){
            return;
        }
        VirtualFile vManifestFile = manifestFile.getVirtualFile();
        if (vManifestFile == null || !ReadonlyStatusHandler.ensureFilesWritable(manifestFile.getProject(),vManifestFile)){
            return;
        }

        XmlTag activityTag = XmlElementFactory.getInstance(myProject).createTagFromText("<activity");
        activityTag.setAttribute("android:name",".TokenActivity");
        XmlTag redActivityTag = XmlElementFactory.getInstance(myProject).createTagFromText("<activity");
        redActivityTag.setAttribute("android:name","net.openid.appauth.RedirectUriReceiverActivity");
        //redActivityTag.setAttribute("tools:node","replace");
        XmlTag intentTag = XmlElementFactory.getInstance(myProject).createTagFromText("<intent-filter");
        if (GenerateClassAuthorization.getInstance().isHasAppLink()){
            intentTag.setAttribute("android:autoVerify","true");}
        XmlTag actionTag = XmlElementFactory.getInstance(myProject).createTagFromText("<action");
        actionTag.setAttribute("android:name","android.intent.action.VIEW");
        XmlTag categoryTag = XmlElementFactory.getInstance(myProject).createTagFromText("<category");
        categoryTag.setAttribute("android:name","android.intent.category.DEFAULT");
        XmlTag categoryTag2 = XmlElementFactory.getInstance(myProject).createTagFromText("<category");
        categoryTag2.setAttribute("android:name","android.intent.category.BROWSABLE");
        XmlTag dataTag = XmlElementFactory.getInstance(myProject).createTagFromText("<data");

        if (GenerateClassAuthorization.getInstance().getPath()!= null){
            dataTag.setAttribute("android:scheme",GenerateClassAuthorization.getInstance().getScheme().substring(0,GenerateClassAuthorization.getInstance().getScheme().indexOf(":")));
            dataTag.setAttribute("android:host", GenerateClassAuthorization.getInstance().getHost());
            dataTag.setAttribute("android:path",GenerateClassAuthorization.getInstance().getPath());
        } else {
            dataTag.setAttribute("android:scheme",GenerateClassAuthorization.getInstance().getScheme().substring(0,GenerateClassAuthorization.getInstance().getScheme().indexOf(":")));
            dataTag.setAttribute("android:host", GenerateClassAuthorization.getInstance().getHost());
        }
        intentTag.addSubTag(actionTag,true);
        intentTag.addSubTag(categoryTag,false);
        intentTag.addSubTag(categoryTag2,false);
        intentTag.addSubTag(dataTag,false);
        redActivityTag.addSubTag(intentTag,true);

        if (GenerateClassAuthorization.getInstance().isHasAppLink()) {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(activityTag, true);});
            WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(redActivityTag, false);});
            CodeStyleManager.getInstance(manifestFile.getProject()).reformat(manifestFile);
        } else {
            WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(activityTag, true);});
            CodeStyleManager.getInstance(manifestFile.getProject()).reformat(manifestFile);
        }

    }

    /**
     * this function is used to add one authorization after authentication (multiple scenario)
     */
    private void inspectAndroidManifestboth() {
        Module[] modules = ModuleManager.getInstance(myProject).getSortedModules();
        Module module = modules[modules.length-2];
        AndroidFacet facet=AndroidFacet.getInstance(module);
        Manifest manifest = facet.getManifest();
        if (manifest==null){
            return;
        }
        final XmlTag manifestTag = manifest.getXmlTag();
        if (manifestTag == null){
            return;
        }

        XmlTag applicationTag = manifestTag.findFirstSubTag("application");
        if (applicationTag==null){
            return;
        }

        final PsiFile manifestFile = manifestTag.getContainingFile();
        if (manifestFile == null){
            return;
        }
        VirtualFile vManifestFile = manifestFile.getVirtualFile();
        if (vManifestFile == null || !ReadonlyStatusHandler.ensureFilesWritable(manifestFile.getProject(),vManifestFile)){
            return;
        }

       // XmlTag activityTag = XmlElementFactory.getInstance(myProject).createTagFromText("<activity");
       // activityTag.setAttribute("android:name",".AuthorizationActivity");
        XmlTag redActivityTag = XmlElementFactory.getInstance(myProject).createTagFromText("<activity");
        redActivityTag.setAttribute("android:name","net.openid.appauth.RedirectUriReceiverActivity");
       // redActivityTag.setAttribute("tools:node","replace");
        XmlTag intentTag = XmlElementFactory.getInstance(myProject).createTagFromText("<intent-filter");
        if (GenerateClassAuthorization.getInstance().isHasAppLink()){
            intentTag.setAttribute("android:autoVerify","true");
        XmlTag actionTag = XmlElementFactory.getInstance(myProject).createTagFromText("<action");
        actionTag.setAttribute("android:name","android.intent.action.VIEW");
        XmlTag categoryTag = XmlElementFactory.getInstance(myProject).createTagFromText("<category");
        categoryTag.setAttribute("android:name","android.intent.category.DEFAULT");
        XmlTag categoryTag2 = XmlElementFactory.getInstance(myProject).createTagFromText("<category");
        categoryTag2.setAttribute("android:name","android.intent.category.BROWSABLE");
        XmlTag dataTag = XmlElementFactory.getInstance(myProject).createTagFromText("<data");
        if(GenerateClassAuthorization.getInstance().getPath()!=null){
            dataTag.setAttribute("android:scheme",GenerateClassAuthorization.getInstance().getScheme().substring(0,GenerateClassAuthorization.getInstance().getScheme().indexOf(":")));
            dataTag.setAttribute("android:host", GenerateClassAuthorization.getInstance().getHost());
            dataTag.setAttribute("android:path",GenerateClassAuthorization.getInstance().getPath());
        } else {
            dataTag.setAttribute("android:scheme",GenerateClassAuthorization.getInstance().getScheme().substring(0,GenerateClassAuthorization.getInstance().getScheme().indexOf(":")));
            dataTag.setAttribute("android:host", GenerateClassAuthorization.getInstance().getHost());
        }

        intentTag.addSubTag(actionTag,true);
        intentTag.addSubTag(categoryTag,false);
        intentTag.addSubTag(categoryTag2,false);
        intentTag.addSubTag(dataTag,false);
        redActivityTag.addSubTag(intentTag,true);}

        if (GenerateClassAuthorization.getInstance().isHasAppLink()) {
           // WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(activityTag, true);});
            WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(redActivityTag, false);});
            CodeStyleManager.getInstance(manifestFile.getProject()).reformat(manifestFile);
        }else {
            //WriteCommandAction.runWriteCommandAction(myProject, () -> {applicationTag.addSubTag(activityTag, true);});
           // CodeStyleManager.getInstance(manifestFile.getProject()).reformat(manifestFile);

        }

    }

    /**
     * this class is for single authorization (single scenario), which is not work in the new android studio
     */
   /* private void inspectBuildGradle() {
        // Get the module object for the current project
        Module[] modules = ModuleManager.getInstance(myProject).getSortedModules();
        //Module[] modules= ModuleManager.getInstance(ProjectManager.getInstance().getOpenProjects()[0]).getSortedModules();
        Module module = modules[modules.length-2];
        GradleBuildModel model = GradleBuildModel.get(module);
        assert model != null;
        AndroidModel androidModel = model.android();
        if (GenerateClassAuthorization.getInstance().isHasCustom()){
            String cutomUrl = GenerateClassAuthorization.getInstance().getCustomURL();
            if (cutomUrl.contains(":") && androidModel!=null){
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf(":"));
                androidModel.defaultConfig().setManifestPlaceholder("appAuthRedirectScheme",cutomUrl);
            }else {
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf("/"));
                assert androidModel != null;
                androidModel.defaultConfig().setManifestPlaceholder("appAuthRedirectScheme", cutomUrl);
            }
        } else {
            assert androidModel != null;
            androidModel.defaultConfig().setManifestPlaceholder("appAuthRedirectScheme", "");
        }
        DependenciesModel dependenciesModel = model.dependencies();
        dependenciesModel.addArtifact(IMPLEMENTATION, "net.openid:appauth:0.7.0");
        dependenciesModel.addArtifact(IMPLEMENTATION, "com.android.support:design:28.0.0");
        dependenciesModel.addArtifact(IMPLEMENTATION, "joda-time:joda-time:2.9.9");
        dependenciesModel.addArtifact(IMPLEMENTATION, "com.squareup.okio:okio:1.14.1");
        dependenciesModel.addArtifact(IMPLEMENTATION, "com.github.bumptech.glide:glide:4.7.1");
        dependenciesModel.addArtifact("annotationProcessor", "com.github.bumptech.glide:compiler:4.7.1");
        WriteCommandAction.runWriteCommandAction(myProject, () -> {model.applyChanges();});
    }*/

    private void inspectBuildGradle() throws IOException {

        String buildFilePath = myProject.getBasePath() + "/app/build.gradle";

        String cutomUrl;

        if (GenerateClassAuthorization.getInstance().isHasCustom()){

            cutomUrl = GenerateClassAuthorization.getInstance().getCustomURL();
            if (cutomUrl.contains(":")) {
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf(":"));

            }else  {
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf("/"));

            }
        }else {

            cutomUrl = "";
        }

        BufferedReader br_build = new BufferedReader(new FileReader(buildFilePath));

        StringBuilder newBuildFile = new StringBuilder();
        String line;

        while ((line = br_build.readLine()) != null) {
            newBuildFile.append(line).append("\n");
            if (line.contains("defaultConfig {")){
                newBuildFile.append("manifestPlaceholders" + " " + "appAuthRedirectScheme:").append("\"").append(cutomUrl).append("\"").append("\n");
            }else if (line.contains("dependencies {")) {
                newBuildFile.append("implementation" + " ").append("\'net.openid:appauth:0.7.0\'").append("\n");
                newBuildFile.append("implementation" + " ").append("\'joda-time:joda-time:2.9.9\'").append("\n");
                newBuildFile.append("implementation" + " ").append("\'com.squareup.okio:okio:1.14.1\'").append("\n");
                newBuildFile.append("implementation" + " ").append("\'com.github.bumptech.glide:glide:4.7.1\'").append("\n");
                newBuildFile.append("annotationProcessor" + " ").append("\'com.github.bumptech.glide:compiler:4.7.1\'").append("\n");
            }
        }
        br_build.close();

        FileWriter fw_build = new FileWriter(buildFilePath);
        fw_build.write(newBuildFile.toString());
        fw_build.close();
    }

    /**
     * this is authorization class for authorization after authentication
     */
    private void inspectBuildGradleboth() throws IOException {

        String buildFilePath = myProject.getBasePath() + "/app/build.gradle";

        String cutomUrl;

        if (GenerateClassAuthorization.getInstance().isHasCustom()){

            cutomUrl = GenerateClassAuthorization.getInstance().getCustomURL();
            if (cutomUrl.contains(":")) {
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf(":"));

            }else  {
                cutomUrl = cutomUrl.substring(0, cutomUrl.indexOf("/"));

            }
        }else {

            cutomUrl = "";
        }

        BufferedReader br_build = new BufferedReader(new FileReader(buildFilePath));

        StringBuilder newBuildFile = new StringBuilder();
        String line;

        while ((line = br_build.readLine()) != null) {
            newBuildFile.append(line).append("\n");
            if (line.contains("defaultConfig {")){
                newBuildFile.append("manifestPlaceholders" + " " + "appAuthRedirectScheme:").append("\"").append(cutomUrl).append("\"").append("\n");
            }
        }
        br_build.close();

        FileWriter fw_build = new FileWriter(buildFilePath);
        fw_build.write(newBuildFile.toString());
        fw_build.close();

    }

    /**
     * the purpose of this class is to read the empty json structure in the specifiec path of developer App
     * then create values from UI and IDP classes and assign it into JSON.
     */
    private void inspectJSONFile() {
        String idpName = GenerateClassAuthorization.getInstance().getIdmName();
        String devDomain, authorizationURL = " ", c_ID = " ", c_Sec=" ", r_URI = " ", scope = " ", tokenURL = " ", resourceURL = " ";
        String JSONPath = myProject.getBasePath() + "/app/src/main/res/raw/authorization_config.json";
        VirtualFile jVFile = LocalFileSystem.getInstance().findFileByPath(JSONPath);
        assert jVFile != null;
        Document document = FileDocumentManager.getInstance().getDocument(jVFile);
        assert document != null;
        PsiFile jFile = PsiDocumentManager.getInstance(myProject).getPsiFile(document);
        JsonElementGenerator jsonElementGenerator = new JsonElementGenerator(myProject);
        System.out.println(idpName);
        Class classINeedAtRuntime;
        try {
            classINeedAtRuntime = Class.forName("eu.fbk.IdMDroid.IDMP.DB." + idpName);
            Constructor constructor = classINeedAtRuntime.getConstructor();
            Object newObject = constructor.newInstance();
            IDMP idmp = (IDMP)newObject;
            devDomain = GenerateClassAuthorization.getInstance().getDevDomain();
            if (devDomain!= null){
                authorizationURL = devDomain + idmp.AuthorizationURL();
                tokenURL = devDomain + idmp.TokenURL();
            } else {

                authorizationURL = idmp.AuthorizationURL();
                tokenURL = idmp.TokenURL();
            }

            c_ID =  GenerateClassAuthorization.getInstance().getAppID();
            if (GenerateClassAuthorization.getInstance().isHasSecret()){
                c_Sec= GenerateClassAuthorization.getInstance().getAppSecret();
            } else{
                c_Sec=" ";
            }
            scope = GenerateClassAuthorization.getInstance().getScope();
            resourceURL = GenerateClassAuthorization.getInstance().getResourceEnd();
            if(GenerateClassAuthorization.getInstance().isHasCustom()){
                r_URI = GenerateClassAuthorization.getInstance().getCustomURL();
            } else if (GenerateClassAuthorization.getInstance().isHasAppLink() & GenerateClassAuthentication.getInstance().getPath()!=null) {
                r_URI = GenerateClassAuthorization.getInstance().getScheme() + GenerateClassAuthorization.getInstance().getHost() + GenerateClassAuthorization.getInstance().getPath();
            } else {
                r_URI = GenerateClassAuthorization.getInstance().getScheme() + GenerateClassAuthorization.getInstance().getHost();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("I dont have the class " + idpName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("I dont have the constructor for the class " + idpName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        PsiElement c_IDElement = jsonElementGenerator.createValue("\"" + c_ID + "\"");
        PsiElement c_SecElement = jsonElementGenerator.createValue("\"" + c_Sec + "\"");
        PsiElement authz_URIElement = jsonElementGenerator.createValue("\"" + authorizationURL + "\"");
        PsiElement token_URIElement = jsonElementGenerator.createValue("\"" + tokenURL + "\"");
        PsiElement resource_URIElement = jsonElementGenerator.createValue("\"" + resourceURL + "\"");
        PsiElement r_URIElement = jsonElementGenerator.createValue("\"" + r_URI + "\"");
        PsiElement scope_Element = jsonElementGenerator.createValue("\"" + scope + "\"");
        if (jFile instanceof JsonFile){
            if (((JsonFile) jFile).getTopLevelValue() instanceof JsonObject){
                JsonObject object = (JsonObject) ((JsonFile) jFile).getTopLevelValue();
                if (object!=null){
                    JsonProperty cID = object.findProperty("client_id");
                    JsonProperty cSec = object.findProperty("client_secret");
                    JsonProperty R_URI = object.findProperty("redirect_uri");
                    JsonProperty Scope = object.findProperty("authorization_scope");
                    JsonProperty Authz_URI = object.findProperty("authorization_endpoint_uri");
                    JsonProperty Token_URI = object.findProperty("token_endpoint_uri");
                    JsonProperty Resource_URI = object.findProperty("resource_endpoint_uri");
                    JsonStringLiteral cIDLiteral = (JsonStringLiteral) cID.getValue();
                    JsonStringLiteral cSecLiteral = (JsonStringLiteral) cSec.getValue();
                    JsonStringLiteral R_URILiteral = (JsonStringLiteral) R_URI.getValue();
                    JsonStringLiteral scopeLiteral = (JsonStringLiteral) Scope.getValue();
                    JsonStringLiteral Authz_URILiteral = (JsonStringLiteral) Authz_URI.getValue();
                    JsonStringLiteral Token_URILiteral = (JsonStringLiteral) Token_URI.getValue();
                    JsonStringLiteral Resource_URILiteral = (JsonStringLiteral) Resource_URI.getValue();
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {cIDLiteral.replace(c_IDElement);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {cSecLiteral.replace(c_SecElement);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {R_URILiteral.replace(r_URIElement);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {scopeLiteral.replace(scope_Element);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {Authz_URILiteral.replace(authz_URIElement);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {Token_URILiteral.replace(token_URIElement);});
                    WriteCommandAction.runWriteCommandAction(myProject, () -> {Resource_URILiteral.replace(resource_URIElement);});
                }
            }
        }

    }
    /**
     * this is the class for single scenario part of authorization
     * @param psiClass
     */
    private void generateCompareTo(PsiClass psiClass) {
        StringBuilder createAuthRequestBuilder = new StringBuilder("private void createAuthRequest() {\n" +
                "        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(\n" +
                "                mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration(),\n" +
                "                mClientId.get(),\n" +
                "                ResponseTypeValues.CODE,\n" +
                "                mConfiguration.getRedirectUri())\n" +
                "                .setScope(mConfiguration.getScope());\n" +
                "        mAuthRequest.set(authRequestBuilder.build());\n" +
                "    }");
        StringBuilder AuthorizationServiceBuilder = new StringBuilder("private AuthorizationService createAuthorizationService() {\n" +
                "        Log.i(TAG, \"Creating authorization service\");\n" +
                "        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();\n" +
                "        builder.setBrowserMatcher(mBrowserMatcher);\n" +
                "        builder.setConnectionBuilder(mConfiguration.getConnectionBuilder());\n" +
                "\n" +
                "        return new AuthorizationService(this, builder.build());\n" +
                "    }");
        StringBuilder recreateAuthorizationServiceBuilder = new StringBuilder("private void recreateAuthorizationService() {\n" +
                "        if (mAuthService != null) {\n" +
                "            Log.i(TAG, \"Discarding existing AuthService instance\");\n" +
                "            mAuthService.dispose();\n" +
                "        }\n" +
                "        mAuthService = createAuthorizationService();\n" +
                "        mAuthRequest.set(null);\n" +
                "        mAuthIntent.set(null);\n" +
                "    }");
        StringBuilder warmUpBrowserBuilder = new StringBuilder("private void warmUpBrowser() {\n" +
                "        mAuthIntentLatch = new CountDownLatch(1);\n" +
                "        Log.i(TAG, \"Warming up browser instance for auth request\");\n" +
                "        CustomTabsIntent.Builder intentBuilder =\n" +
                "                mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());\n" +
                "        mAuthIntent.set(intentBuilder.build());\n" +
                "        mAuthIntentLatch.countDown();\n" +
                "    }");
        StringBuilder initializeAuthReqBuilder = new StringBuilder("private void initializeAuthRequest() {\n" +
                "        createAuthRequest();\n" +
                "        warmUpBrowser();\n" +
                "    }");
        StringBuilder initilizeClient = new StringBuilder("private void initializeClient() {\n" +
                "        if (mConfiguration.getClientId() != null) {\n" +
                "            Log.i(TAG, \"Using static client ID: \" + mConfiguration.getClientId());\n" +
                "            // use a statically configured client ID\n" +
                "            mClientId.set(mConfiguration.getClientId());\n" +
                "            initializeAuthRequest();\n" +
                "        }\n" +
                "    }");
        StringBuilder handelConfigResult = new StringBuilder("private void handleConfigurationRetrievalResult(\n" +
                "            AuthorizationServiceConfiguration config,\n" +
                "            AuthorizationException ex) {\n" +
                "        if (config == null) {\n" +
                "            Log.i(TAG, \"Failed to retrieve discovery document\", ex);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        Log.i(TAG, \"Discovery document retrieved\");\n" +
                "        mAuthStateManager.replace(new AuthState(config));\n" +
                "        initializeClient();\n" +
                "    }");
        StringBuilder initialAppAuth = new StringBuilder("private void initializeAppAuth() {\n" +
                "        Log.i(TAG, \"Initializing AppAuth\");\n" +
                "        recreateAuthorizationService();\n" +
                "\n" +
                "        if (mAuthStateManager.getCurrent().getAuthorizationServiceConfiguration() != null) {\n" +
                "            // configuration is already created, skip to client initialization\n" +
                "            Log.i(TAG, \"auth config already established\");\n" +
                "            initializeClient();\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        // if we are not using discovery, build the authorization service configuration directly\n" +
                "        // from the static configuration values.\n" +
                "        if (mConfiguration.getDiscoveryUri() == null) {\n" +
                "            Log.i(TAG, \"Creating auth config from res/raw/auth_config.json\");\n" +
                "            AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(\n" +
                "                    mConfiguration.getAuthEndpointUri(),\n" +
                "                    mConfiguration.getTokenEndpointUri(),\n" +
                "                    mConfiguration.getRegistrationEndpointUri());\n" +
                "\n" +
                "            mAuthStateManager.replace(new AuthState(config));\n" +
                "            initializeClient();\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        // WrongThread inference is incorrect for lambdas\n" +
                "        // noinspection WrongThread\n" +
                "        Log.i(TAG, \"Retrieving OpenID discovery doc\");\n" +
                "        AuthorizationServiceConfiguration.fetchFromUrl(\n" +
                "                mConfiguration.getDiscoveryUri(),\n" +
                "                this::handleConfigurationRetrievalResult);\n" +
                "    }");
        StringBuilder doAuth = new StringBuilder("private void doAuth() {\n" +
                "        try {\n" +
                "            mAuthIntentLatch.await();\n" +
                "        } catch (InterruptedException ex) {\n" +
                "            Log.w(TAG, \"Interrupted while waiting for auth intent\");\n" +
                "        }\n" +
                "\n" +
                "            Intent intent = mAuthService.getAuthorizationRequestIntent(\n" +
                "                    mAuthRequest.get(),\n" +
                "                    mAuthIntent.get());\n" +
                "            startActivityForResult(intent, RC_AUTH);\n" +
                "    }");
        StringBuilder startAuth = new StringBuilder("void startAuth() {\n" +
                "\n" +
                "        doAuth();\n" +
                "    }");
        StringBuilder onActivityResult = new StringBuilder("@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {\n" +
                "\n" +
                "        Intent intent = new Intent(this, TokenActivity.class);\n" +
                "        intent.putExtras(data.getExtras());\n" +
                "        startActivity(intent);\n" +
                "    }");
        StringBuilder tag = new StringBuilder("private static final String TAG = \"LoginActivity\";\n");
        StringBuilder RcAuth = new StringBuilder("private static final int RC_AUTH = 100;\n");
        StringBuilder Var = new StringBuilder("private AuthorizationService mAuthService;\n");
        StringBuilder Var1 = new StringBuilder("private AuthStateManager mAuthStateManager;\n");
        StringBuilder Var2 = new StringBuilder("private Configuration mConfiguration;\n");
        StringBuilder Var3 = new StringBuilder("private final AtomicReference<String> mClientId = new AtomicReference<>();\n");
        StringBuilder Var4 = new StringBuilder("private final AtomicReference<AuthorizationRequest> mAuthRequest = new AtomicReference<>();\n");
        StringBuilder Var5 = new StringBuilder("private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();\n");
        StringBuilder Var6 = new StringBuilder("private CountDownLatch mAuthIntentLatch = new CountDownLatch(1);\n");
        StringBuilder Var7 = new StringBuilder("@NonNull\n" +
                "    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;\n");
        StringBuilder Var8 = new StringBuilder("Button " + GenerateClassAuthorization.getInstance().getBtnName() + ";");
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod createAuthRequest= elementFactory.createMethodFromText(createAuthRequestBuilder.toString(),psiClass);
        PsiMethod AuthorizationService= elementFactory.createMethodFromText(AuthorizationServiceBuilder.toString(),psiClass);
        PsiMethod recreateAuthorizationService= elementFactory.createMethodFromText(recreateAuthorizationServiceBuilder.toString(),psiClass);
        PsiMethod warmUpBrowser= elementFactory.createMethodFromText(warmUpBrowserBuilder.toString(),psiClass);
        PsiMethod initializeAuth= elementFactory.createMethodFromText(initializeAuthReqBuilder.toString(),psiClass);
        PsiMethod initializeClient= elementFactory.createMethodFromText(initilizeClient.toString(),psiClass);
        PsiMethod handleConfig= elementFactory.createMethodFromText(handelConfigResult.toString(),psiClass);
        PsiMethod initialappAuth= elementFactory.createMethodFromText(initialAppAuth.toString(),psiClass);
        PsiMethod doauth= elementFactory.createMethodFromText(doAuth.toString(),psiClass);
        PsiMethod startauth= elementFactory.createMethodFromText(startAuth.toString(),psiClass);
        PsiMethod onactivityResult= elementFactory.createMethodFromText(onActivityResult.toString(),psiClass);
        PsiStatement psiTag = elementFactory.createStatementFromText(tag.toString(),psiClass);
        PsiStatement psiRcAuth = elementFactory.createStatementFromText(RcAuth.toString(),psiClass);
        PsiStatement var = elementFactory.createStatementFromText(Var.toString(),psiClass);
        PsiStatement var1 = elementFactory.createStatementFromText(Var1.toString(),psiClass);
        PsiStatement var2 = elementFactory.createStatementFromText(Var2.toString(),psiClass);
        PsiStatement var3 = elementFactory.createStatementFromText(Var3.toString(),psiClass);
        PsiStatement var4 = elementFactory.createStatementFromText(Var4.toString(),psiClass);
        PsiStatement var5 = elementFactory.createStatementFromText(Var5.toString(),psiClass);
        PsiStatement var6 = elementFactory.createStatementFromText(Var6.toString(),psiClass);
        PsiStatement var7 = elementFactory.createStatementFromText(Var7.toString(),psiClass);
        PsiStatement var8 = elementFactory.createStatementFromText(Var8.toString(),psiClass);
        PsiElement createAuth = psiClass.add(createAuthRequest);
        PsiElement authService = psiClass.add(AuthorizationService);
        PsiElement recreateAuth = psiClass.add(recreateAuthorizationService);
        PsiElement warmBrowser = psiClass.add(warmUpBrowser);
        PsiElement initialAuth = psiClass.add(initializeAuth);
        PsiElement initialClnt = psiClass.add(initializeClient);
        PsiElement handleConf = psiClass.add(handleConfig);
        PsiElement initappAuth = psiClass.add(initialappAuth);
        PsiElement doauthReq = psiClass.add(doauth);
        PsiElement strtauth = psiClass.add(startauth);
        PsiElement onActivityRes = psiClass.add(onactivityResult);
        PsiElement tage = psiClass.addBefore(psiTag,createAuth);
        PsiElement rcauth = psiClass.addAfter(psiRcAuth,tage);
        PsiElement varr = psiClass.addAfter(var,rcauth);
        PsiElement varr1 = psiClass.addAfter(var1,varr);
        PsiElement varr2 = psiClass.addAfter(var2,varr1);
        PsiElement varr3 = psiClass.addAfter(var3,varr2);
        PsiElement varr4 = psiClass.addAfter(var4,varr3);
        PsiElement varr5 = psiClass.addAfter(var5,varr4);
        PsiElement varr6 = psiClass.addAfter(var6,varr5);
        PsiElement varr7 = psiClass.addAfter(var7,varr6);
        PsiElement varr8 = psiClass.addAfter(var8,varr7);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(createAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(authService);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(recreateAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(warmBrowser);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initialAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initialClnt);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(handleConf);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initappAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(doauthReq);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(strtauth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(onActivityRes);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(tage);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(rcauth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr1);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr2);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr3);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr4);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr5);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr6);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr7);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr8);
    }

    private void generateCompareToboth(PsiClass psiClass) {

        StringBuilder init = new StringBuilder("void init(Bundle savedInstanceState) {\n" +
                "\n" +
                "        if (savedInstanceState != null) {\n" +
                "           try {\n" +
                "                    mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));\n" +
                "               } catch (JSONException ex) {\n"+
                "                   Log.e(TAG, \"Failed to parse saved user info JSON, discarding\", ex);\n" +
                "               }\n" +
                " }\n" +
                "mExecutor = Executors.newSingleThreadExecutor();\n " +
                "mAuthStateManagerAuthorization = AuthStateManagerAuthorization.getInstance(this);\n" +
                "mConfigurationAuthorization = ConfigurationAuthorization.getInstance(this);\n" +
                GenerateClassAuthorization.getInstance().getBtnName() + " = (Button) findViewById(R.id."+ GenerateClassAuthorization.getInstance().getBtnName()+");\n" +
                GenerateClassAuthorization.getInstance().getBtnName()+".setOnClickListener(new View.OnClickListener() {\n" +
                "@Override\n" +
                "public void onClick(View v) {\n" +
                "   startAuthorization();\n" +
                "}\n" +
                "});\n" +
                "initializeAppAuthAuthorization();\n" +
                "    }");

        StringBuilder createAuthRequestBuilder = new StringBuilder("private void createAuthRequestAuthorization() {\n" +
                "        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(\n" +
                "                mAuthStateManagerAuthorization.getCurrent().getAuthorizationServiceConfiguration(),\n" +
                "                mClientId.get(),\n" +
                "                ResponseTypeValues.CODE,\n" +
                "                mConfigurationAuthorization.getRedirectUri())\n" +
                "                .setScope(mConfigurationAuthorization.getScope());\n" +
                "        mAuthRequestAuthorization.set(authRequestBuilder.build());\n" +
                "    }");
        StringBuilder AuthorizationServiceBuilder = new StringBuilder("private AuthorizationService createAuthorizationServiceAuthorization() {\n" +
                "        Log.i(TAG, \"Creating authorization service\");\n" +
                "        AppAuthConfiguration.Builder builder = new AppAuthConfiguration.Builder();\n" +
                "        builder.setBrowserMatcher(mBrowserMatcher);\n" +
                "        builder.setConnectionBuilder(mConfigurationAuthorization.getConnectionBuilder());\n" +
                "\n" +
                "        return new AuthorizationService(this, builder.build());\n" +
                "    }");
        StringBuilder recreateAuthorizationServiceBuilder = new StringBuilder("private void recreateAuthorizationServiceAuthorization() {\n" +
                "        if (mAuthServiceAuthorization != null) {\n" +
                "            Log.i(TAG, \"Discarding existing AuthService instance\");\n" +
                "            mAuthServiceAuthorization.dispose();\n" +
                "        }\n" +
                "        mAuthServiceAuthorization = createAuthorizationServiceAuthorization();\n" +
                "        mAuthRequestAuthorization.set(null);\n" +
                "        mAuthIntent.set(null);\n" +
                "    }");
        StringBuilder warmUpBrowserBuilder = new StringBuilder("private void warmUpBrowserAuthorization() {\n" +
                "        mAuthIntentLatch = new CountDownLatch(1);\n" +
                "        Log.i(TAG, \"Warming up browser instance for auth request\");\n" +
                "        CustomTabsIntent.Builder intentBuilder =\n" +
                "                mAuthServiceAuthorization.createCustomTabsIntentBuilder(mAuthRequestAuthorization.get().toUri());\n" +
                "        mAuthIntent.set(intentBuilder.build());\n" +
                "        mAuthIntentLatch.countDown();\n" +
                "    }");
        StringBuilder initializeAuthReqBuilder = new StringBuilder("private void initializeAuthRequestAuthorization() {\n" +
                "        createAuthRequestAuthorization();\n" +
                "        warmUpBrowserAuthorization();\n" +
                "    }");
        StringBuilder initilizeClient = new StringBuilder("private void initializeClientAuthorization() {\n" +
                "        if (mConfigurationAuthorization.getClientId() != null) {\n" +
                "            Log.i(TAG, \"Using static client ID: \" + mConfigurationAuthorization.getClientId());\n" +
                "            // use a statically configured client ID\n" +
                "            mClientId.set(mConfigurationAuthorization.getClientId());\n" +
                "            initializeAuthRequestAuthorization();\n" +
                "        }\n" +
                "    }");
        StringBuilder handelConfigResult = new StringBuilder("private void handleConfigurationRetrievalResultAuthorization(\n" +
                "            AuthorizationServiceConfiguration configAuthorization,\n" +
                "            AuthorizationException ex) {\n" +
                "        if (configAuthorization == null) {\n" +
                "            Log.i(TAG, \"Failed to retrieve discovery document\", ex);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        Log.i(TAG, \"Discovery document retrieved\");\n" +
                "        mAuthStateManagerAuthorization.replace(new AuthState(configAuthorization));\n" +
                "        initializeClientAuthorization();\n" +
                "    }");
        StringBuilder initialAppAuth = new StringBuilder("private void initializeAppAuthAuthorization() {\n" +
                "        Log.i(TAG, \"Initializing AppAuth\");\n" +
                "        recreateAuthorizationServiceAuthorization();\n" +

                "        // if we are not using discovery, build the authorization service configuration directly\n" +
                "        // from the static configuration values.\n" +
                "        if (mConfigurationAuthorization.getDiscoveryUri() == null) {\n" +
                "            Log.i(TAG, \"Creating auth config from res/raw/auth_config_Authorization.json\");\n" +
                "            AuthorizationServiceConfiguration configAuthorization = new AuthorizationServiceConfiguration(\n" +
                "                    mConfigurationAuthorization.getAuthEndpointUri(),\n" +
                "                    mConfigurationAuthorization.getTokenEndpointUri(),\n" +
                "                    mConfigurationAuthorization.getRegistrationEndpointUri());\n" +
                "\n" +
                "            mAuthStateManagerAuthorization.replace(new AuthState(configAuthorization));\n" +
                "            initializeClientAuthorization();\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        // WrongThread inference is incorrect for lambdas\n" +
                "        // noinspection WrongThread\n" +
                "        Log.i(TAG, \"Retrieving OpenID discovery doc\");\n" +
                "        AuthorizationServiceConfiguration.fetchFromUrl(\n" +
                "                mConfigurationAuthorization.getDiscoveryUri(),\n" +
                "                this::handleConfigurationRetrievalResultAuthorization);\n" +
                "    }");
        StringBuilder doAuth = new StringBuilder("private void doAuthAuthorization() {\n" +
                "        try {\n" +
                "            mAuthIntentLatch.await();\n" +
                "        } catch (InterruptedException ex) {\n" +
                "            Log.w(TAG, \"Interrupted while waiting for auth intent\");\n" +
                "        }\n" +
                "\n" +
                "            Intent intent = mAuthServiceAuthorization.getAuthorizationRequestIntent(\n" +
                "                    mAuthRequestAuthorization.get(),\n" +
                "                    mAuthIntent.get());\n" +
                "            startActivityForResult(intent, RC_AUTH);\n" +
                "    }");
        StringBuilder startAuth = new StringBuilder("void startAuthorization() {\n" +
                "\n" +
                "        doAuthAuthorization();\n" +
                "    }");
        StringBuilder performTokenRequest = new StringBuilder("private void performTokenRequestAuthorization(\n" +
                "            TokenRequest request,\n" +
                "            AuthorizationService.TokenResponseCallback callback) {\n" +
                "ClientAuthentication clientAuthentication;\n" +
                "        try {\n" +
                "            clientAuthentication = mAuthStateManagerAuthorization.getCurrent().getClientAuthentication();\n" +
                "        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {\n" +
                "           Log.d(TAG, \"Token request cannot be made, client authentication for the token \"\n" +
                "                    + \"endpoint could not be constructed (%s)\", ex);\n" +
                "            displayNotAuthorized(\"Client authentication method is unsupported\");\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "            mAuthServiceAuthorization.performTokenRequest(\n" +
                "                request,\n" +
                "                clientAuthentication,\n" +
                "                callback);\n"+
                "    }");

        StringBuilder displayAuthorized = new StringBuilder("private void displayAuthorized() {\n" +
                "\n" +
                "        AuthState stateAuthorization = mAuthStateManagerAuthorization.getCurrent();\n" +
                "        TextView refreshTokenInfoView = (TextView) findViewById(R.id.refresh_token_info);\n" +
                "        refreshTokenInfoView.setText((stateAuthorization.getRefreshToken() == null)\n" +
                "                ? \"No refresh token returned\"\n" +
                "                : \"Refresh token returned\");\n" +
                " TextView idTokenInfoView = (TextView) findViewById(R.id.id_token_info);\n" +
                "        idTokenInfoView.setText((stateAuthorization.getIdToken()) == null\n" +
                "                ? \"No ID Token returned\"\n" +
                "                : \"ID Token returned\");\n" +
                " TextView accessTokenInfoView = (TextView) findViewById(R.id.access_token_info);" +
                "if (stateAuthorization.getAccessToken() == null) {\n" +
                "       accessTokenInfoView.setText(\"No access token returned\");\n" +
                "} else { \n" +
                "   Long expiresAt = stateAuthorization.getAccessTokenExpirationTime();" +
                "    if (expiresAt == null) { \n" +
                "       accessTokenInfoView.setText(\"Access time has no defined expiry\");\n" +
                "    } else if (expiresAt < System.currentTimeMillis()) { \n" +
                "       accessTokenInfoView.setText(\"Access Token has expired\");" +
                "    } else { \n" +
                "String template = \"Access Token expires at: %s\";\n" +
                "                accessTokenInfoView.setText(String.format(template,\n" +
                "                        DateTimeFormat.forPattern(\"yyyy-MM-dd HH:mm:ss ZZ\").print(expiresAt)));\n" +
                "   }\n" +
                "}\n" +
                " View userInfoCard = findViewById(R.id.userinfo_card);\n" +
                "        userInfoCard.setVisibility(View.VISIBLE);\n" +
                "        JSONObject userInfo = mUserInfoJson.get();\n" +
                "if (userInfo == null) { \n" +
                "   Log.i(TAG, \"EMPTY\");\n" +
                "} else { \n" +
                "/// please add your code here in order to parse the JSON Response\n" +
                "}\n" +
                "    }");

        StringBuilder showSnack = new StringBuilder("private void showSnackbar(String message) {\n" +
                "\n" +
                "        Snackbar.make(findViewById(R.id.coordinator),\n" +
                "                message,\n" +
                "                Snackbar.LENGTH_SHORT)\n" +
                "                .show();\n" +
                "    }");

        StringBuilder fetchUser = new StringBuilder("private void fetchUserInfoAuthorization() {\n" +
                "\n" +
                "        displayLoading(\"Fetching user info\");\n" +
                "        mAuthStateManagerAuthorization.getCurrent().performActionWithFreshTokens(mAuthServiceAuthorization, this::fetchUserInfoAuthorization);\n" +
                "    }");

        StringBuilder fetchUserInfo = new StringBuilder("private void fetchUserInfoAuthorization(String accessToken, String idToken, AuthorizationException ex) {\n" +
                "\n" +
                "if (ex != null) {\n" +
                "            Log.e(TAG, \"Token refresh failed when fetching user info\");\n" +
                "            mUserInfoJson.set(null);\n" +
                "            runOnUiThread(this::displayAuthorized);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        AuthorizationServiceDiscovery discoveryAuthorization =\n" +
                "                mAuthStateManagerAuthorization.getCurrent()\n" +
                "                        .getAuthorizationServiceConfiguration()\n" +
                "                        .discoveryDoc;\n" +
                "\n" +
                "        URL resourceEndpoint;\n" +
                "        try {\n" +
                "            resourceEndpoint =\n" +
                "                    mConfigurationAuthorization.getResourceEndpointUri() != null\n" +
                "                            ? new URL(mConfigurationAuthorization.getResourceEndpointUri().toString())\n" +
                "                            : new URL(discoveryAuthorization.getUserinfoEndpoint().toString());\n" +
                "        } catch (MalformedURLException urlEx) {\n" +
                "            Log.e(TAG, \"Failed to construct resource info endpoint URL\", urlEx);\n" +
                "            mUserInfoJson.set(null);\n" +
                "            runOnUiThread(this::displayAuthorized);\n" +
                "            return;\n" +
                "        }\n" +
                "\n" +
                "        mExecutor.submit(() -> {\n" +
                "            try {\n" +
                "                HttpURLConnection conn =\n" +
                "                        (HttpURLConnection) resourceEndpoint.openConnection();\n" +
                "                conn.setRequestProperty(\"Authorization\", \"Bearer \" + accessToken);\n" +
                "                conn.setInstanceFollowRedirects(false);\n" +
                "                String response = Okio.buffer(Okio.source(conn.getInputStream()))\n" +
                "                        .readString(Charset.forName(\"UTF-8\"));\n" +
                "                mUserInfoJson.set(new JSONObject(response));\n" +
                "            } catch (IOException ioEx) {\n" +
                "                Log.e(TAG, \"Network error when querying userinfo endpoint\", ioEx);\n" +
                "                showSnackbar(\"Fetching user info failed\");\n" +
                "            } catch (JSONException jsonEx) {\n" +
                "                Log.e(TAG, \"Failed to parse userinfo response\");\n" +
                "                showSnackbar(\"Failed to parse user info\");\n" +
                "            }\n" +
                "\n" +
                "            runOnUiThread(this::displayAuthorized);\n" +
                "        });\n" +
                "    }");

        StringBuilder handleCodeEx = new StringBuilder("private void handleCodeExchangeResponseAuthorization(\n" +
                "            @Nullable TokenResponse tokenResponse,\n" +
                "            @Nullable AuthorizationException authException) {\n" +
                "\n" +
                "mAuthStateManagerAuthorization.updateAfterTokenResponse(tokenResponse, authException);\n" +
                "        if (!mAuthStateManagerAuthorization.getCurrent().isAuthorized()) {\n" +
                "            final String message = \"Authorization Code exchange failed\"\n" +
                "                    + ((authException != null) ? authException.error : \"\");\n" +
                "\n" +
                "            // WrongThread inference is incorrect for lambdas\n" +
                "            //noinspection WrongThread\n" +
                "            runOnUiThread(() -> displayNotAuthorized(message));\n" +
                "        } else {\n" +
                "            runOnUiThread(this::displayAuthorized);\n" +
                "            runOnUiThread(this::fetchUserInfoAuthorization);\n" +
                "        }\n" +
                "    }");

        StringBuilder exchangeAuthCode = new StringBuilder("private void exchangeAuthorizationCodeAuthorization(AuthorizationResponse authorizationResponse) {\n" +
                    "\n" +
                    "displayLoading(\"Exchanging authorization code\");\n" +
                    "        performTokenRequestAuthorization(\n" +
                    "                authorizationResponse.createTokenExchangeRequest(),\n" +
                    "                this::handleCodeExchangeResponseAuthorization);\n" +
                    "    }");
        StringBuilder exchangeAuthCodeSec = new StringBuilder("private void exchangeAuthorizationCodeAuthorization(AuthorizationResponse authorizationResponse) {\n" +
                "\n" +
                "displayLoading(\"Exchanging authorization code\");\n" +
                "HashMap<String, String> additionalParams = new HashMap<>();\n"+
                "additionalParams.put(\"client_secret\", mConfigurationGoogle.getClientSecret());\n"+
                "        performTokenRequestAuthorization(\n" +
                "                authorizationResponse.createTokenExchangeRequest(additionalParams),\n" +
                "                this::handleCodeExchangeResponseAuthorization);\n" +
                "    }");

        StringBuilder startTest = new StringBuilder("void authorizationStart(Intent data) {\n" +

                "AuthorizationResponse response = AuthorizationResponse.fromIntent(data);\n" +
                "AuthorizationException ex = AuthorizationException.fromIntent(data);\n" +
                "   if ((response != null || ex != null)) {\n" +
                "            mAuthStateManagerAuthorization.updateAfterAuthorization(response, ex);\n" +
                "        }\n" +
                "        if (response != null && response.authorizationCode != null) {\n" +
                "            mAuthStateManagerAuthorization.updateAfterAuthorization(response, ex);\n" +
                "            exchangeAuthorizationCodeAuthorization(response);\n" +
                "        } else if (ex != null) {\n" +
                "            displayNotAuthorized(\"Authorization flow failed: \");\n" +
                "        } else {\n" +
                "            displayNotAuthorized(\"No authorization state retained - reauthorization required\");\n" +
                "        }\n" +
                "    }");
        StringBuilder displayLoading = new StringBuilder("private void displayLoading(String message) {\n" +

        "}");

        StringBuilder displayNotAuthorized = new StringBuilder("private void displayNotAuthorized(String explanation) {\n" +

                "}");

        StringBuilder onActivityResult = new StringBuilder("@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {\n" +
                "\n" +
                "init(null);\n" +
                "        authorizationStart(data);\n" +
                "    }");
        StringBuilder tag = new StringBuilder("private static final String TAG = \"TokenActivity\";\n");
        StringBuilder RcAuth = new StringBuilder("private static final int RC_AUTH = 200;\n");
        StringBuilder Var = new StringBuilder("private AuthorizationService mAuthServiceAuthorization;\n");
        StringBuilder Var1 = new StringBuilder("private AuthStateManagerGoogle mAuthStateManagerAuthorization;\n");
        StringBuilder Var2 = new StringBuilder("private ConfigurationGoogle mConfigurationAuthorization;\n");
        StringBuilder Var3 = new StringBuilder("private final AtomicReference<String> mClientId = new AtomicReference<>();\n");
        StringBuilder Var4 = new StringBuilder("private final AtomicReference<AuthorizationRequest> mAuthRequestAuthorization = new AtomicReference<>();\n");
        StringBuilder Var5 = new StringBuilder("private final AtomicReference<CustomTabsIntent> mAuthIntent = new AtomicReference<>();\n");
        StringBuilder Var6 = new StringBuilder("private CountDownLatch mAuthIntentLatch = new CountDownLatch(1);\n");
        StringBuilder Var7 = new StringBuilder("@NonNull\n" +
                "    private BrowserMatcher mBrowserMatcher = AnyBrowserMatcher.INSTANCE;\n");
        StringBuilder Var8 = new StringBuilder("Button " + GenerateClassAuthorization.getInstance().getBtnName() + ";");
        StringBuilder Var9 = new StringBuilder("private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();\n");
        StringBuilder Var10 = new StringBuilder("private static final String KEY_USER_INFO = \"userInfo\";\n");
        StringBuilder Var11 = new StringBuilder("private ExecutorService mExecutor;\n");

        String idpName = GenerateClassAuthorization.getInstance().getIdmName();
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
        PsiMethod createAuthRequest= elementFactory.createMethodFromText(createAuthRequestBuilder.toString(),psiClass);
        PsiMethod AuthorizationService= elementFactory.createMethodFromText(AuthorizationServiceBuilder.toString(),psiClass);
        PsiMethod recreateAuthorizationService= elementFactory.createMethodFromText(recreateAuthorizationServiceBuilder.toString(),psiClass);
        PsiMethod warmUpBrowser= elementFactory.createMethodFromText(warmUpBrowserBuilder.toString(),psiClass);
        PsiMethod initializeAuth= elementFactory.createMethodFromText(initializeAuthReqBuilder.toString(),psiClass);
        PsiMethod initializeClient= elementFactory.createMethodFromText(initilizeClient.toString(),psiClass);
        PsiMethod handleConfig= elementFactory.createMethodFromText(handelConfigResult.toString(),psiClass);
        PsiMethod initialappAuth= elementFactory.createMethodFromText(initialAppAuth.toString(),psiClass);
        PsiMethod doauth= elementFactory.createMethodFromText(doAuth.toString(),psiClass);
        PsiMethod startauth= elementFactory.createMethodFromText(startAuth.toString(),psiClass);
        PsiMethod onactivityResult= elementFactory.createMethodFromText(onActivityResult.toString(),psiClass);
        PsiMethod initz= elementFactory.createMethodFromText(init.toString(),psiClass);
        PsiMethod performtokenRequest= elementFactory.createMethodFromText(performTokenRequest.toString(),psiClass);
        PsiMethod displayauthorized= elementFactory.createMethodFromText(displayAuthorized.toString(),psiClass);
        PsiMethod showsnack= elementFactory.createMethodFromText(showSnack.toString(),psiClass);
        PsiMethod fetchuser= elementFactory.createMethodFromText(fetchUser.toString(),psiClass);
        PsiMethod fetchuserInfo= elementFactory.createMethodFromText(fetchUserInfo.toString(),psiClass);
        PsiMethod handlecodeEx= elementFactory.createMethodFromText(handleCodeEx.toString(),psiClass);
        PsiMethod exchangeauthCode;
        if ( idpName.equals("Box")  || idpName.equals("DropBox") || idpName.equals("Yahoo") ) {
             exchangeauthCode= elementFactory.createMethodFromText(exchangeAuthCodeSec.toString(),psiClass);
        } else {
             exchangeauthCode= elementFactory.createMethodFromText(exchangeAuthCode.toString(),psiClass);
        }
        PsiMethod starttest= elementFactory.createMethodFromText(startTest.toString(),psiClass);
        PsiMethod displayloading= elementFactory.createMethodFromText(displayLoading.toString(),psiClass);
        PsiMethod displaynotAuthorized= elementFactory.createMethodFromText(displayNotAuthorized.toString(),psiClass);
        PsiStatement psiTag = elementFactory.createStatementFromText(tag.toString(),psiClass);
        PsiStatement psiRcAuth = elementFactory.createStatementFromText(RcAuth.toString(),psiClass);
        PsiStatement var = elementFactory.createStatementFromText(Var.toString(),psiClass);
        PsiStatement var1 = elementFactory.createStatementFromText(Var1.toString(),psiClass);
        PsiStatement var2 = elementFactory.createStatementFromText(Var2.toString(),psiClass);
        PsiStatement var3 = elementFactory.createStatementFromText(Var3.toString(),psiClass);
        PsiStatement var4 = elementFactory.createStatementFromText(Var4.toString(),psiClass);
        PsiStatement var5 = elementFactory.createStatementFromText(Var5.toString(),psiClass);
        PsiStatement var6 = elementFactory.createStatementFromText(Var6.toString(),psiClass);
        PsiStatement var7 = elementFactory.createStatementFromText(Var7.toString(),psiClass);
        PsiStatement var8 = elementFactory.createStatementFromText(Var8.toString(),psiClass);
        PsiStatement var9 = elementFactory.createStatementFromText(Var9.toString(),psiClass);
        PsiStatement var10 = elementFactory.createStatementFromText(Var10.toString(),psiClass);
        PsiStatement var11 = elementFactory.createStatementFromText(Var11.toString(),psiClass);
        PsiElement createAuth = psiClass.add(createAuthRequest);
        PsiElement authService = psiClass.add(AuthorizationService);
        PsiElement recreateAuth = psiClass.add(recreateAuthorizationService);
        PsiElement warmBrowser = psiClass.add(warmUpBrowser);
        PsiElement initialAuth = psiClass.add(initializeAuth);
        PsiElement initialClnt = psiClass.add(initializeClient);
        PsiElement handleConf = psiClass.add(handleConfig);
        PsiElement initappAuth = psiClass.add(initialappAuth);
        PsiElement doauthReq = psiClass.add(doauth);
        PsiElement strtauth = psiClass.add(startauth);
        PsiElement onActivityRes = psiClass.add(onactivityResult);
        PsiElement tage = psiClass.addBefore(psiTag,createAuth);
        PsiElement rcauth = psiClass.addAfter(psiRcAuth,tage);
        PsiElement initFunc = psiClass.add(initz);
        PsiElement performtokenRequestFunc = psiClass.add(performtokenRequest);
        PsiElement displayauthorizedFunc = psiClass.add(displayauthorized);
        PsiElement showsnackFunc = psiClass.add(showsnack);
        PsiElement fetchuserFunc = psiClass.add(fetchuser);
        PsiElement fetchuserInfoFunc = psiClass.add(fetchuserInfo);
        PsiElement handlecodeExFunc = psiClass.add(handlecodeEx);
        PsiElement exchangeauthCodeFunc = psiClass.add(exchangeauthCode);
        PsiElement startTestFunc = psiClass.add(starttest);
        PsiElement displayloadingFunc = psiClass.add(displayloading);
        PsiElement displaynotFunc = psiClass.add(displaynotAuthorized);
        PsiElement varr = psiClass.addAfter(var,rcauth);
        PsiElement varr1 = psiClass.addAfter(var1,varr);
        PsiElement varr2 = psiClass.addAfter(var2,varr1);
        PsiElement varr3 = psiClass.addAfter(var3,varr2);
        PsiElement varr4 = psiClass.addAfter(var4,varr3);
        PsiElement varr5 = psiClass.addAfter(var5,varr4);
        PsiElement varr6 = psiClass.addAfter(var6,varr5);
        PsiElement varr7 = psiClass.addAfter(var7,varr6);
        PsiElement varr8 = psiClass.addAfter(var8,varr7);
        PsiElement varr9 = psiClass.addAfter(var9,varr8);
        PsiElement varr10 = psiClass.addAfter(var10,varr9);
        PsiElement varr11 = psiClass.addAfter(var11,varr10);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(createAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(authService);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(recreateAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(warmBrowser);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initialAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initialClnt);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(handleConf);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(initappAuth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(doauthReq);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(strtauth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(onActivityRes);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(performtokenRequestFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(displayauthorizedFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(showsnackFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(fetchuserFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(fetchuserInfoFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(handlecodeExFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(exchangeauthCodeFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(startTestFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(displayloadingFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(displaynotFunc);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(tage);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(rcauth);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr1);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr2);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr3);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr4);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr5);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr6);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr7);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr8);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr9);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr10);
        JavaCodeStyleManager.getInstance(psiClass.getProject()).shortenClassReferences(varr11);
        insertCodeHandlerboth = new InsertCodeHandlerAuthz(psiclass);
        insertCodeHandlerboth.invoke(myProject);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiClass psiclass = getPsiClassFromContext(e);
        PsiFile psiFile = psiclass.getContainingFile();
        psiDirectory = psiFile.getContainingDirectory();
        e.getPresentation().setEnabled(psiclass != null);
    }

    private PsiClass getPsiClassFromContext(@NotNull AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            e.getPresentation().setEnabled(false);
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass psiclass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        return psiclass;
    }
}

