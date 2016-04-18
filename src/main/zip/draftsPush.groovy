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
def definition   = props['definition']
def replace      = props['replace']
def productOnly  = Boolean.valueOf(props['productOnly'])
def apicPath     = props['apicPath']

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
    args = [apicPath, "drafts:push", definition, "--server", server, "--organization", organization]
    if (replace) {
        args << "--replace"
        args << replace
    }
    if (productOnly) {
        args << "--product-only"
    }
}
else {
    if (isWindows) {
        args = ["cmd", "/C"]
    }
    else {
        args = ["/bin/bash", "-c"]
    }
    def apicCommand = "apic drafts:push ${definition} --server ${server} --organization ${organization}"
    if (replace) {
        apicCommand += " --replace ${replace}"
    }
    if (productOnly) {
        apicCommand += " --product-only"
    }
    args << apicCommand
}

def exitCode
try {
    exitCode = ch.runCommand("[Action] Pushing API or definitions to drafts...", args, helper.getSystemOutput)
}
catch (IOException ex) {
    println "[Error] Unable to find the 'apic' command line tool."
    println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
                "Installation directions can be found on the API Connect troubleshooting documentation page."
    println "[Possible Solution] If you properties contain spaces, surround the properties with quotes. "
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
    println output

    // Iterate through the possible outputs
    if (output.contains("ERROR Use 'apic login' to log into")){
        println "[Possible Solution] Begin the process with the 'Login' step."
        System.exit(1)
    }
    else if (output.contains("${definition} does not exist.")){
        println "[Error] Unable to run the 'apic drafts:push' command because the ${definition} file could not be found."
        println "[Possible Solution] Confirm the definition  file is a valid property."
        println "[Possible Solution] If you properties contain spaces, surround the properties with quotes. "
        System.exit(1)
    }
    else if (output.contains("ERROR")){
        println "[Error] Unable to run the 'apic drafts:push' command."
        println "[Possible Solution] Confirm the server, organization, and definition properties are valid."
        println "[Possible Solution] If you properties contain spaces, surround the properties with quotes. "
        System.exit(1)
    }
    else {
        println "[OK] Successfully pushed definition to API Connect cloud."
    }
}