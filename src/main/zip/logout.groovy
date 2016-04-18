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

def server   = props['server']
def apicPath = props['apicPath']

final def isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
    args = [apicPath, "logout", "--server", server]
}
else {
    if (isWindows) {
        args = ["cmd", "/C"]
    }
    else {
        args = ["/bin/bash", "-c"]
    }
    args << "apic logout --server ${server}"
}

def exitCode
try {
    exitCode = ch.runCommand("[Action] Clear authentication with '${server}'...", args, helper.getSystemOutput)
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
    println output

    // Logout failed because incorrect server or username never logged in... assume success
    if (output.contains("ERROR User not logged in")){
        println "[Warning] A user was never authenticated with the server or an incorrect server name was entered."
    }
    else if (output.contains("ERROR")){
        println "[Error] Unable to run the 'apic logout' command."
        println "[Possible Solution] Confirm the server property is valid."
        System.exit(1)
    }
    else {
        println "[OK] Authentication clear completed successfully."
    }
}