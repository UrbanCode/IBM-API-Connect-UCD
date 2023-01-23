# IBM API Connect plug-in for IBM UrbanCode Deploy
---
Note: This is not the plugin distributable! This is the source code. To find the installable plugin, go to the plug-in page on the [IBM UrbanCode Plug-ins microsite](https://urbancode.github.io/IBM-UCx-PLUGIN-DOCS/UCD).

### License
This plug-in is protected under the [Eclipse Public 1.0 License](http://www.eclipse.org/legal/epl-v10.html)

### Overview
IBM API Connect plug-in for IBM UrbanCode Deploy provides steps to publish API Definitions and Loopback Apps to an API Connect server. This plug-in communicates to API Connect through the use of the apiconnect (apic) command line toolkit.

### Documentation
All plug-in documentation is updated and maintained on the [IBM UrbanCode Plug-ins microsite](https://urbancode.github.io/IBM-UCx-PLUGIN-DOCS/UCD/apiconnect/).

### Support
Plug-ins downloaded directly from the [IBM UrbanCode Plug-ins microsite](https://urbancode.github.io/IBM-UCx-PLUGIN-DOCS/UCD/) are fully supported by IBM. Create a GitHub Issue or Pull Request for minor requests and bug fixes. For time sensitive issues that require immediate assistance, [file a PMR](https://www-947.ibm.com/support/servicerequest/newServiceRequest.action) through the normal IBM support channels. Plug-ins built externally (like this one) or modified with custom code are supported on a best-effort-basis using GitHub Issues.

### Locally Build the Plug-in
This open source plug-in uses Gradle as its build tool. [Install the latest version of Gradle](https://gradle.org/install) to build the plug-in locally. Build the plug-in by running the `gradle` command in the plug-in's root directory. The plug-in distributable will be placed under the `build/distributions` folder.
