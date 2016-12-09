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
def catalog      = props['catalog']
def definition   = props['definition']
def stage        = Boolean.valueOf(props['stage'])
def apicPath     = props['apicPath']

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
    args = [apicPath, "products:publish", definition, "--server", server, "--organization", organization, "--catalog", catalog]
    if (stage) {
        args << "--stage"
    }
}
else {
    if (isWindows) {
        args = ["cmd", "/C"]
    }
    else {
        args = ["/bin/bash", "-c"]
    }
    def apicCommand = "apic products:publish ${definition} --server ${server} --organization ${organization} --catalog ${catalog}"
    if (stage) {
        apicCommand += " --stage"
    }
    args << apicCommand
}

def exitCode
try {
    exitCode = ch.runCommand("[Action] Publishing API or definitions to catalog...", args, helper.getSystemOutput)
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
    println "================================"
    println "API Connect Output..."
    println output
    println "================================"

    // Regex is determine if the output contains the word "Error" - case insensitive.
    if (output.matches(".*(?i)Error(.|\\n)*")){
        println "[Error] Unable to run the 'apic publish' command."
        println "[Possible Solution] Begin the process with the 'Login' step."
        println "[Possible Solution] Confirm the definition file is a valid property or it doesn't already exist."
        println "[Possible Solution] Confirm the server, organization, catalog, and definition properties are valid."
        println "[Possible Solution] If you properties contain spaces, surround the properties with quotes. "
        System.exit(1)
    }
    else {
        println "[OK] Successfully published definition to API Connect cloud's catalog."
    }
}
