/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2016. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper
def apTool = new AirPluginTool(this.args[0], this.args[1]) 

def props = apTool.getStepProperties()

def server 		 = props['server']
def organization = props['organization']
def app	 		 = props['app']
def local		 = Boolean.valueOf(props['local'])
def global   	 = Boolean.valueOf(props['global'])

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
	args = [apicPath, "config:set",
				local ? "" : "--local",		// local is true, print "--local", else null
				global ? "" : "--global",	// global is true, print "--global", else null
				"app=apic-app://${server}/orgs/${organization}/apps/${app}"
		   ]
}
else {
	if (isWindows) {
		args = ["cmd", "/C"]
	}
	else {
		args = ["/bin/bash", "-c"]
	}
	args << "apic config:set " +
			local ? "" : "--local " +
			global ? "" : "--global " +
			"app=apic-app://${server}/orgs/${organization}/apps/${app}"
}

def exitCode 
try {
	exitCode = ch.runCommand("[Action] Setting the '${app}' app configuration variable...", args)
}
catch (IOException ex) {
	println "[Error] Unable to find the 'apic' command line tool."
	println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
				"Installation directions can be found on the API Connect troubleshooting documentation page."
	ex.printStackTrace()
	System.exit(1)
}
catch (Exception ex) {
	println "[Error] Unable to run the 'apic config:set' command."
	println "[Possible Solution] Confirm the server, organization, app name properties are correct."
	ex.printStackTrace()
	System.exit(1)
}

println "[OK] Successfully set the '${app}' app configuration variable."
