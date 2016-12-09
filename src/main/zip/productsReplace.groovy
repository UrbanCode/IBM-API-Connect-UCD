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

def oldProduct   = props['oldProduct']
def newProduct   = props['newProduct']
def plans        = props['plans']
def catalog      = props['catalog']
def server       = props['server']
def organization = props['organization']
def apicPath     = props['apicPath']

final Boolean isWindows = System.getProperty('os.name').contains("Windows")

APICHelper helper = new APICHelper()
File workDir = new File(".")
CommandHelper ch = new CommandHelper(workDir)

List<String> properties = ["products:replace", oldProduct, newProduct,
        "--plans", plans,
        "--catalog", catalog,
        "--server", server,
        "--organization", organization
        ]

List<String> args = helper.constructCommand(apicPath, properties)

try {
    ch.runCommand("[Action] Replacing '${oldProduct}' with '${newProduct}' in Catalog...", args, helper.getSystemOutput)
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
        println "[Error] Unable to run the 'apic product:replace' command."
        println "[Possible Solution] Begin the process with the 'Login' step."
        println "[Possible Solution] Confirm the server, organization, and product properties are valid."
        System.exit(1)
    }
    else {
        println "[OK] Successfully replaced the product '${oldProduct}' with ${newProduct} in API Connect."
    }
}
