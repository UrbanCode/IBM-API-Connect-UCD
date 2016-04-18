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

    // Closure to get the output from the dscmds
    def systemOutput = ""
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

}