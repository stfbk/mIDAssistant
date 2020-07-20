# mIDAssistant

**mIDAssistant** is an Android Studio plugin that guides native mobile app developers with secure integeration of Single Sign-On Login ([OpenID Connect](http://openid.net/specs/openid-connect-core-1_0.html)) and Access Delegation ([OAuth 2.0](https://tools.ietf.org/html/rfc6749)) solutions within their apps. The companion page can be found [here](https://sites.google.com/fbk.eu/130720-paper). 

## Features

**mIDAssistant** provides a wizard-based approach that guides developers to integrate multiple third-party Identity Management providers (IdMP) within their native apps. The mIDAssistant Plugin aims to support native app developers for integration of IdMPs which are either fully-compliant with the [RFC 8252](https://tools.ietf.org/html/rfc8252), or which are currently not fully compliant with RFC 8252 but that can be still used in a secure manner. The current version of mIDAssistant is able to:

  - Enforce the usage of best current practices (BCP) for native apps set out in
    [RFC 8252 - OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252) with thanks to the integration of [AppAuth](https://appauth.io).
  - Avoid the need to download several SDKs and understanding their online documentations (a list of known
    IdMPs with their configuration information is embedded within our tool).
  - Automatically integrating the secure customized code to enable the communication with the different IdMPs.
  - Support Amazon, Auth0, Google, IBM, Microsoft, OKTA, Ping, and Yahoo as OpenID Connect IdMPs.
  - Support Box, DropBox, Google, and Microsoft as OAuth 2.0 IdMPs. 

## Requirements

mIDassistant demands the following requirments:

- It supports Android API 16 (Jellybean) and above (AppAuth SDK requirement).
- HTTPS App Link redirection for Android API 23 and above.

## Download

You can install mIDAssistant by downloading the jar file from the Github repository.

##mIDAssistant Installation

In order to install mIDAssistant within the Android Studio environment, developers should perform the following steps:

- In the Android Studio IDE, click on the android studio menu tab → preferences → Plugins 
- Select install plugin from disk → locate the mIDAssistant jar file → select → apply
- Restart the Android Studio IDE

## Setup Phase

Developers must perform the following steps: 

Developers create an empty Android project with min SDK version 16 within Android studio that contains:

- Create an activity for either Single Sign-On or Access Deleagtion (in the case of single button either for Single Sign-On or Access Delegation), or create two activities (one for Single Sign-On and one for Access Delegation) for the case that developers start the integeration of IdMPs within their apps from the scratch, otherwise they just need to navigate into the activity(ies) that they want to add Single Sign-on /Access Delegation button(s). 
- Create the Layout files related to the Single Sign-On, and/or Access Delegation activities.
- Create "raw" folder within the following path ("src/app/main/res/raw").
- Developers installed the plugin within Android Studio (This step will happen just once).


## Usage

Developers should perform the steps in the setup phase beforehand. Then, developers should navigate into the targeted activity (the activity they are going to add either the Single Sign-On or Access delegation Button) and within the ``onCreate()`` method. Developers can select the scenario to integrate by clicking on the mIDAssistant tab and select one of the possible options (Single Sign-On Login / Access Delegation). Based on the selection, developers are shown with a GUI that contains a list of OpenID Connect / OAuth 2.0 IdMPs and configuration questions. 

Based on the IdMP selection, varied questions will populate within the GUI. Developers should provide the necessary infromation to provide the AppAuth customize code and automatically integrate the secure code within developers app. Indeed, developers are asking to provide the following configuration information:

- Scopes (optional): the authorization scopes that should be added within the Access Token.
- Client ID: the application specific identifier that developers will obtain after the registration of the app in the IdMPs developer dashboard. 
- Button Name: It is the button name that declared by the developer in the correlated activity layout file. 
- Developer Domain: Some of the IdMPs append a developer specific domain URL into the endpoints. In this case, developers are asked to provide the domain specific URL within the GUI. 
- Redirection method: Developers can select the prefered method from the available IdMPs supported redirection methods.
- Valid Domain URL: In the case of the HTTPS redirection scheme, developers must provide scheme, host, and path value.  
- Custom URL: In case of the Custom URL redirection scheme, developers must provide the Custom URL. 

A demo video providing an overview of using the mIDAssitant plugin for integration of OpenID Connect provider (OKTA) besides an OAuth 2.0 provider (Google) can be accessed [here](https://drive.google.com/file/d/1n8an-oAIeWM4bqw6Oz-cQXpd8zwG6HLj/view?usp=sharing).

## Limitations 

The current prototype of the mIDAssistant has the following limitations:

- Developers must create a folder at a specific location ("src/app/main/res/raw").
- The integrated code for incorporating multiple IdMPs is not clean.

## License
Copyright 2019-2020, Fondazione Bruno Kessler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Developed within [Security & Trust](https://st.fbk.eu) Research Unit at [Fondazione Bruno Kessler](https://www.fbk.eu/en/) (Italy)
