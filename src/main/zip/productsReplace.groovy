/**
 * (c) Copyright IBM Corporation 2016, 2017.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import com.urbancode.air.AirPluginTool
import com.urbancode.air.CommandHelper
import com.urbancode.air.ExitCodeException
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
        // Bug in apic products:replace.
        // It does not like the full names for catalog, server, and organization
        "-c", catalog,
        "-s", server,
        "-o", organization
    ]

List<String> args = helper.constructCommand(apicPath, properties)

try {
    ch.runCommand("[Action] Replacing '${oldProduct}' with '${newProduct}' in Catalog...", args)
}
catch (IOException ex) {
    println ""
    println "[Error] Unable to find the 'apic' command line tool."
    println "[Possible Solution] Confirm the 'apic' command line tool is installed. " +
                "Installation directions can be found on the API Connect troubleshooting documentation page."
    ex.printStackTrace()
    System.exit(1)
}
catch (ExitCodeException ex) {
    println ""
    println "[Error] Unable to successfully complete the apic command. Review the above error for help."
    println "[Possible Solution] Attempt to run the above apic command manually on the agent's terminal."
    ex.printStackTrace()
    System.exit(1)
}
catch (Exception ex) {
    println ""
    println "[Error] Unable to successfully complete the apic command."
    ex.printStackTrace()
    System.exit(1)
}

println "[OK] Successfully replaced the product '${oldProduct}' with ${newProduct} in API Connect."
