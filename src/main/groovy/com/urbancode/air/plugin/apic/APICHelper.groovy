/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2016. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

package com.urbancode.air.plugin.apic

public class APICHelper {

    final String APIC = "apic"
    final def isWindows = System.getProperty('os.name').contains("Windows")

    // Closure to get the output from the apic commands
    // Deprecated: Updates to apic now print expected output to terminal
    // Use productsReplace.groovy for current example
    String systemOutput = ""
    public def getSystemOutput = {Process proc ->
        def out = new PrintStream(System.out, true)
        def outputStream = new StringBuilder()
        try {
            proc.waitForProcessOutput(outputStream, out)
        }
        finally {
            out.flush();
        }
        systemOutput = outputStream.toString()
    }

    public List<String> constructCommand(String apicPath, List<String> properties) {
        List<String> result = []

        if (apicPath && apicPath != APIC) {
            File apicCmd = new File(apicPath)
            if (!apicCmd.isFile()) {
                String newApicPath = apicPath
                if (!apicPath.endsWith(File.separator)) {
                    newApicPath += File.separator
                }
                if (isWindows) {
                    newApicPath += "apic.cmd"
                } else {
                    newApicPath += "apic"
                }
                apicCmd = new File(newApicPath)
                if (!apicCmd.isFile()) {
                    throw new FileNotFoundException("[Error] The specified APIC Tool Path '${apicPath}' " +
                        "does not exist at or within this location.")
                }
            }
            result << apicCmd.getCanonicalPath()
            result += properties
        } else {
            if (isWindows) {
                result = ["cmd", "/C"]
                // Add full command as single argument
                result << APIC + " " + properties.join(" ")
            }
            else {
                // Add full command as individual arguments
                result << APIC
                result += properties
            }
        }
        return result
    }
}
