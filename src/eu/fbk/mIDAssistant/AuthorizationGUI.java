package eu.fbk.mIDAssistant;

import eu.fbk.mIDAssistant.IDMP.DB.*;
import eu.fbk.mIDAssistant.IDMP.DB.Box;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The code related to populate the Authorization GUI
 * @author amirsh
 */

public class AuthorizationGUI {
    private JPanel Root;
    private JLabel STAnD;
    private JLabel IDMPSelection;
    private JLabel ConfigurationInfo;
    private JLabel IDMPRedirection;
    private JLabel HTTPSScheme;
    private JLabel CustomURI;
    private JTextField CustomURLTxt;
    private JLabel ClientSecret;
    private JTextField AppSecretTxt;
    private JTextField ScopeTxt;
    private JTextField AppIDTxt;
    private JTextField ButtonTxt;
    private JLabel SchemeTag;
    private JLabel HostTag;
    private JLabel PathTag;
    private JTextField SchemeTxt;
    private JTextField HostTxt;
    private JTextField PathTxt;
    private JLabel ScopeTag;
    private JLabel AppIDTag;
    private JLabel ButtonTag;
    private JRadioButton HTTPSButton;
    private JRadioButton customURLButton;
    private JComboBox IDMPBox;
    private JLabel CustomURLTag;
    private JLabel AppSecretTag;
    private JTextField RscEndTxt;
    private JLabel ResourceEndPointTag;
    private JTextField DevDomainTxt;
    private JLabel DevTag;

    /**
     * the constructor of Authorization Class that populate the GUI based on
     * the IdP that is selected by the user
     */
    public AuthorizationGUI(){
        IDMPBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selected = (String) IDMPBox.getSelectedItem();
                GenerateClassAuthorization.getInstance().setIdmName(selected);
                assert selected != null;
                switch (selected) {
                    case "Box": {
                        IDMP idmp = new Box();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }
                        break;
                    }
                    case "Yahoo": {
                        IDMP idmp = new Yahoo();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }
                        break;
                    }
                    case "Amazon": {
                        IDMP idmp = new Amazon();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }

                        break;
                    }
                    case "DropBox": {
                        IDMP idmp = new DropBox();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }
                        break;
                    }
                    case "Google": {
                        IDMP idmp = new Google();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }
                        break;
                    }
                    /*case "Linkedin": {
                        IDMP idmp = new Linkedin();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }

                        break;
                    }*/
                    default: {
                        IDMP idmp = new Microsoft();
                        switch (idmp.Redirection()) {
                            case "both":
                                IDMPRedirection.setVisible(true);
                                HTTPSButton.setVisible(true);
                                customURLButton.setVisible(true);
                                //hide the https if activated by previous option
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                // hide the custom if activated by previous provider
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            case "HTTPS":
                                // hide the selection of HTTPS or Custom
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //activate Https
                                HTTPSScheme.setVisible(true);
                                SchemeTag.setVisible(true);
                                HostTag.setVisible(true);
                                PathTag.setVisible(true);
                                SchemeTxt.setVisible(true);
                                HostTxt.setVisible(true);
                                PathTxt.setVisible(true);
                                //hide custom
                                CustomURI.setVisible(false);
                                CustomURLTag.setVisible(false);
                                CustomURLTxt.setVisible(false);
                                break;
                            default:
                                //hide the selection
                                IDMPRedirection.setVisible(false);
                                HTTPSButton.setVisible(false);
                                customURLButton.setVisible(false);
                                //hide https
                                HTTPSScheme.setVisible(false);
                                SchemeTag.setVisible(false);
                                HostTag.setVisible(false);
                                PathTag.setVisible(false);
                                SchemeTxt.setVisible(false);
                                HostTxt.setVisible(false);
                                PathTxt.setVisible(false);
                                //activate Custom
                                CustomURI.setVisible(true);
                                CustomURLTag.setVisible(true);
                                CustomURLTxt.setVisible(true);
                                break;
                        }
                        if (idmp.ClientSecret().equals("yes")) {
                            ClientSecret.setVisible(true);
                            AppSecretTag.setVisible(true);
                            AppSecretTxt.setVisible(true);
                        } else {
                            ClientSecret.setVisible(false);
                            AppSecretTag.setVisible(false);
                            AppSecretTxt.setVisible(false);
                        }
                        if (idmp.DevDomain().equals("yes")) {
                            DevTag.setVisible(true);
                            DevDomainTxt.setVisible(true);
                        } else {
                            DevTag.setVisible(false);
                            DevDomainTxt.setVisible(false);
                        }
                        break;
                    }
                }
            }
        });

        HTTPSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (HTTPSButton.isSelected()){
                    customURLButton.setEnabled(false);
                    GenerateClassAuthorization.getInstance().setHasCustom(false);
                    GenerateClassAuthorization.getInstance().setHasAppLink(true);
                    HTTPSScheme.setVisible(true);
                    SchemeTag.setVisible(true);
                    HostTag.setVisible(true);
                    PathTag.setVisible(true);
                    SchemeTxt.setVisible(true);
                    HostTxt.setVisible(true);
                    PathTxt.setVisible(true);
                } else {
                    customURLButton.setEnabled(true);
                    HTTPSScheme.setVisible(false);
                    SchemeTag.setVisible(false);
                    HostTag.setVisible(false);
                    PathTag.setVisible(false);
                    SchemeTxt.setVisible(false);
                    HostTxt.setVisible(false);
                    PathTxt.setVisible(false);
                    GenerateClassAuthorization.getInstance().setHasAppLink(false);
                }

            }
        });
        customURLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (customURLButton.isSelected()){
                    HTTPSButton.setEnabled(false);
                    GenerateClassAuthorization.getInstance().setHasAppLink(false);
                    GenerateClassAuthorization.getInstance().setHasCustom(true);
                    CustomURI.setVisible(true);
                    CustomURLTag.setVisible(true);
                    CustomURLTxt.setVisible(true);
                } else {
                    HTTPSButton.setEnabled(true);
                    CustomURI.setVisible(false);
                    CustomURLTag.setVisible(false);
                    CustomURLTxt.setVisible(false);
                    GenerateClassAuthorization.getInstance().setHasCustom(false);
                }

            }
        });

        // Read the Scheme text field of HTTPS Redirection
        SchemeTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScheme(SchemeTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScheme(SchemeTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScheme(SchemeTxt.getText());
            }
        });
        // Read the Host text field of HTTPS Redirection
        HostTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHost(HostTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHost(HostTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHost(HostTxt.getText());
            }
        });
        // Read the Path text field of HTTPS Redirection
        PathTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setPath(PathTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setPath(PathTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setPath(PathTxt.getText());
            }
        });
        // Read the Scope text field
        ScopeTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScope(ScopeTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScope(ScopeTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setScope(ScopeTxt.getText());
            }
        });
        // Read the Client ID
        AppIDTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setAppID(AppIDTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setAppID(AppIDTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setAppID(AppIDTxt.getText());
            }
        });
        //Read the Button Name
        ButtonTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setBtnName(ButtonTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setBtnName(ButtonTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setBtnName(ButtonTxt.getText());
            }
        });
        //read the developer domain name
        DevDomainTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setDevDomain(DevDomainTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setDevDomain(DevDomainTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setDevDomain(DevDomainTxt.getText());
            }
        });
        //read the Custom URL value from the text field
        CustomURLTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setCustomURL(CustomURLTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setCustomURL(CustomURLTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setCustomURL(CustomURLTxt.getText());
            }
        });
        // read the Client Secret from the text field.
        AppSecretTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHasSecret(true);
                GenerateClassAuthorization.getInstance().setAppSecret(AppSecretTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHasSecret(true);
                GenerateClassAuthorization.getInstance().setAppSecret(AppSecretTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setHasSecret(true);
                GenerateClassAuthorization.getInstance().setAppSecret(AppSecretTxt.getText());
            }
        });

        // read the Resource Endpoint from the Related GUI Text Field
        RscEndTxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setResourceEnd(RscEndTxt.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setResourceEnd(RscEndTxt.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                GenerateClassAuthorization.getInstance().setResourceEnd(RscEndTxt.getText());
            }
        });

    }

    /**
     *
     * @return GUI Root Panel
     */
    public JPanel getContent(){
        return Root;
    }
}
