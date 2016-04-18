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
import com.urbancode.air.plugin.apic.APICHelper
def apTool = new AirPluginTool(this.args[0], this.args[1]) 

def props = apTool.getStepProperties()

def app		 	 = props['app']
def server   	 = props['server']
def organization = props['organization']
def apicPath	 = props['apicPath']

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
	args = [apicPath, "apps:publish", "--app", app, "--server", server, "--organization", organization]
}
else {
	if (isWindows) {
		args = ["cmd", "/C"]
	}
	else {
		args = ["/bin/bash", "-c"]
	}
	args << "apic apps:publish --app $app --server $server --organization $organization"
}

def exitCode 
try {
	exitCode = ch.runCommand("[Action] Publishing a Node.js application to a provider app...", args, helper.getSystemOutput)
}
catch (IOException ex) {
	println "[Error] Unable to find the 'apic' command line tool."
	println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
				"Installation directions can be found on the API Connect troubleshooting documentation page."
	ex.printStackTrace()
	System.exit(1)
}
catch (Exception ex) {
	println "[Error] Unable to run the 'apic apps:publish' command."
	println "[Possible Solution] Confirm the server, organization, and app properties are correct."
	ex.printStackTrace()
	System.exit(1)
}

// Print system output
def output = helper.systemOutput
println output

// Iterate through the possible outputs
if (output.contains("ERROR Use 'apic login --type app' to log into")){
    println "[Possible Solution] Begin the process with the 'Login' step."
	System.exit(1)
}
else if (output.contains("ERROR Unsupported project type in")){
	println "[Error] Unable to run the 'apic apps:publish' command."
	println "[Possible Solution] Confirm the app property is valid."
	System.exit(1)
}
else if (output.contains("ERROR")){
	println "[Error] Unable to run the 'apic apps:publish' command."
	println "[Possible Solution] Confirm the server, organization, and app properties are valid."
	System.exit(1)
}
else {
	println "[OK] Successfully published application to API Connect cloud's provider app."
}