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

    // Closure to get the output from the dscmds
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
                throw new FileNotFoundException("[Error] The specified APIC Tool Path property '${apicPath}' does not exist.")
            }
            result = [apicCmd.canonicalPath] + properties;
        } else {
            if (isWindows) {
                result = ["cmd", "/C"]
            }
            else {
                result = ["/bin/sh", "-c"]
            }
            String command = APIC
            properties.each {it ->
                command  += " " + it
            }
            result << command
        }
        return result
    }
}
