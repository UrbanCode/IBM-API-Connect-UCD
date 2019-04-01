/*
* Licensed Materials - Property of IBM Corp.
* IBM UrbanCode Deploy
* (c) Copyright IBM Corporation 2016. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

package com.urbancode.air.plugin.apic

import org.apache.log4j.Logger

import com.urbancode.air.CommandHelper
import com.urbancode.air.ExitCodeException

public abstract class APICHelper {
    protected static Logger logger
    boolean isWindows
    String apicPath
    String server
    String versionCmd
    File workDir

    protected APICHelper(String apicPath, String server) {
        logger = Logger.getLogger(getClass())
        isWindows = System.getProperty('os.name').contains("Windows")

        this.apicPath = apicPath
        this.server = server
        workDir = new File('.')
    }

    public static APICHelper createInstance(apicPath, server) {
        APICHelper helper = new APICv2018Helper(apicPath, server)

        if (helper.isValidToolkit()) {
            logger.info("Detected API Connect version 2018.x Toolkit.")
            return helper
        }
        else {
            helper = new APICv5Helper(apicPath, server)

            if (helper.isValidToolkit()) {
                logger.info("Detected API Connect version 5.x Toolkit.")
                return helper
            }
            else {
                throw new ExitCodeException("APIC toolkit with path '${apicPath}' is invalid.")
            }
        }
    }

    public void logout() {
        List<String> args = ["logout", "--server", server]

        try {
            runCmd(args)
        }
        catch (ExitCodeException ex) {
            logger.error("[Error] Unable to run the 'apic logout' command.")
            logger.error("[Possible Solution] Confirm the server address.")
        }
    }

    public void setConfigVariable(
        String organization,
        String type,
        String name,
        boolean global,
        boolean local)
    {
        List<String> args = [apicPath, "config:set"]

        if (global) {
            args.add("--global")
        }
        if (local) {
            args.add("--local")
        }

        args.add("${type}=apic-${type}://${server}/orgs/${organization}/${type}s/${name}")

        try {
            logger.info("Setting the '${name}' ${type} configuration variable.")
            runCmd(args)
            logger.info("Successfully set the '${name}' ${type} configuration variable.")
        }
        catch (ExitCodeException ex) {
            logger.error("Unable to run the 'apic config:set' command because of an invalid " +
                "property.")
            logger.error("[Possible Solution] Begin the process with the 'Login' step.")
            logger.error("[Possible Solution] Confirm the server, organization, and app " +
                "properties are valid.")
            throw ex
        }
    }

    public boolean isValidToolkit() {
        List<String> args = [versionCmd]
        try {
            runCmd(args, true)
        }
        catch (ExitCodeException ex) {
            return false
        }

        return true
    }

    protected String runCmd(List<String> args) {
        runCmd(args, false)
    }

    protected String runCmd(List<String> args, boolean returnOutput) {
        CommandHelper ch = new CommandHelper(workDir)
        String output

        List<String> cmdArgs
        if (!apicPath) {
            apicPath = "apic"
            args.add(0, apicPath)

            if (isWindows) {
                cmdArgs = ["cmd", "/C"]
            }
            else {
                cmdArgs = ["/bin/bash", "-c"]
            }

            cmdArgs << args.join(' ')
        }
        else {
            args.add(0, apicPath)
            cmdArgs = args
        }

        if (returnOutput) {
            output = execCmdReturnOutput(cmdArgs)
        }
        else {
            execCmd(cmdArgs)
        }

        return output
    }

    public void execCmd(List<String> args) {
        CommandHelper ch = new CommandHelper(workDir)

        try {
            logger.info("APIC Toolkit Output:")
            ch.runCommand(null, args)
        }
        catch (IOException ex) {
            logger.error("Unable to find the 'apic' command line tool.")
            logger.info("[Possible Solution] Confirm the 'apic' command line tool is installed. " +
                        "Installation directions can be found on the API Connect troubleshooting " +
                        "documentation page.")
            throw ex
        }
        finally {
            /* End APIC Toolkit Output section */
            println()
        }
    }

    public String execCmdReturnOutput(List<String> args) {
        ProcessBuilder pb = new ProcessBuilder(args).directory(workDir)
        Process proc = pb.start()
        StringBuffer outputBuffer = new StringBuffer()

        proc.waitForProcessOutput(outputBuffer, outputBuffer)
        String output = outputBuffer.toString()

        if (proc.exitValue()) {
            throw new ExitCodeException("Failed to run command '${args}'. Response: ${output}.")
        }

        return output
    }

    public abstract void login(String username, String password, String realm)
    public abstract void pushApiDraft(String organization, String definition, String replace,
        boolean productOnly)
    public abstract void publishProduct(String organization, String catalog, String definition,
        String space, boolean stage)
    public abstract void publishApp(File projDir, String app, String organization)
    public abstract void replaceProduct(String oldProduct, String newProduct, List<String> plans,
        String catalog, String organization, String space)
}
