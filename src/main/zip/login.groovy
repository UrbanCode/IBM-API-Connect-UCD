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

def server   = props['server']
def username = props['username']
def password = props['password']
def type     = props['type']
def apicPath = props['apicPath']

final def isWindows = System.getProperty('os.name').contains("Windows")

def workDir = new File(".")
def ch = new CommandHelper(workDir)

def args = []
if (apicPath) {
    args = [apicPath, "login", "--server", server, "--username", username, "--password", password]
    if (type != "Default") {
        args << "--type"
        args << type
    }
}
else {
    if (isWindows) {
        args = ["cmd", "/C"]
    }
    else {
        args = ["/bin/bash", "-c"]
    }
    def apicCommand = "apic login --server ${server} --username ${username} --password ${password}"
    if (type != "Default") {
        apicCommand += " --type ${type}"
    }
    args << apicCommand
}

def exitCode
try {
    exitCode = ch.runCommand("[Action] Authenticating with '${server}'...", args)
}
catch (IOException ex) {
    println "[Error] Unable to find the 'apic' command line tool."
    println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
                "Installation directions can be found on the API Connect troubleshooting documentation page."
    ex.printStackTrace()
    System.exit(1)
}
catch (Exception ex) {
    println "[Error] Unable to run the 'apic login' command."
    println "[Possible Solution] Confirm server, username, and password properties are correct."
    println "[Possible Solution] 'Unauthorized grant type...' error may indicate that the configuration properties are incorrectly set."
    ex.printStackTrace()
    System.exit(1)
}

println "[OK] Authentication completed successfully."