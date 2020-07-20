package eu.fbk.IdMDroid;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class GenerateDialogAuthorization extends DialogWrapper {

    private AuthorizationGUI authorizationGUI;

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    public GenerateDialogAuthorization(PsiClass psiClass) {
        super(psiClass.getProject());
        getPreferredSize();
        authorizationGUI = new AuthorizationGUI();
        init();
        setTitle("welcome to the mIDAssistant plugin for Access Delegation");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return authorizationGUI.getContent();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return super.doValidate();
    }
}
