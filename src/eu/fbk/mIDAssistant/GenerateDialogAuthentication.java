package eu.fbk.mIDAssistant;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class GenerateDialogAuthentication extends DialogWrapper {

    private AuthenticationGUI authenticationGUI;

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    public GenerateDialogAuthentication(PsiClass psiClass) {
        super(psiClass.getProject());
        getPreferredSize();
        authenticationGUI = new AuthenticationGUI();
        init();
        setTitle("welcome to the mIDAssistant plugin for SSO Login");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return authenticationGUI.getContent();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return super.doValidate();
    }
}
