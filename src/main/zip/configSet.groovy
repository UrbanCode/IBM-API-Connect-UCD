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

def server       = props['server']
def organization = props['organization']
def type         = props['type']
def name         = props['name']
def apicPath     = props['apicPath']
def local = ""
def global = ""
if (Boolean.valueOf(props['local'])) {
    local = "--local"
}
if (Boolean.valueOf(props['global'])) {
    global = "--global"
}

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
    args = [apicPath, "config:set", local, global,
                "${type}=apic-${type}://${server}/orgs/${organization}/${type}s/${name}"
           ]
}
else {
    if (isWindows) {
        args = ["cmd", "/C"]
    }
    else {
        args = ["/bin/bash", "-c"]
    }
    args << "apic config:set ${local} ${global} ${type}=apic-${type}://${server}/orgs/${organization}/${type}s/${name}"
}

def exitCode
try {
    exitCode = ch.runCommand("[Action] Setting the '${name}' ${type} configuration variable...", args, helper.getSystemOutput)
}
catch (IOException ex) {
    println "[Error] Unable to find the 'apic' command line tool."
    println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
                "Installation directions can be found on the API Connect troubleshooting documentation page."
    ex.printStackTrace()
    System.exit(1)
}
catch (Exception ex) {
    // Error caught later
    ex.printStackTrace()
}
finally {
    // Print system output
    def output = helper.systemOutput
    println "================================"
    println "API Connect Output..."
    println output
    println "================================"

    // Regex is determine if the output contains the word "Error" - case insensitive.
    if (output.matches(".*(?i)Error(.|\\n)*")){
        println "[Error] Unable to run the 'apic config:set' command because of an invalid property."
        println "[Possible Solution] Begin the process with the 'Login' step."
        println "[Possible Solution] Confirm the server, organization, and app properties are valid."
        System.exit(1)
    }
    else {
        println "[OK] Successfully set the '${name}' ${type} configuration variable."
    }
}