package com.urbancode.air.plugin.apic

import com.urbancode.air.ExitCodeException
import org.apache.log4j.Logger

class APICv5Helper extends APICHelper {

    public APICv5Helper(String apicPath, String server) {
        super(apicPath, server)
        this.versionCmd = "-v"
        logger = Logger.getLogger(getClass())
    }

    @Override
    public void login(String username, String password, String realm) {
        if (realm) {
            logger.warn("Ignoring realm value '${realm}' since the v5 toolkit is being used.")
        }

        login(username, password)
    }

    public void login(String username, String password) {
        List<String> args = ["login", "--server", server, "--username", username,
            "--password", password]

        try {
            logger.info("Authenticating against '${server}' with username '${username}'.")
            runCmd(args)
            logger.info("Authentication completed successfully.")
        }
        catch (ExitCodeException ex) {
            logger.error("Unable to run the 'apic login' command.")
            logger.info("[Possible Solution] Confirm server, username, and password properties " +
                "are correct.")
            logger.info("[Possible Solution] 'Unauthorized grant type...' error may indicate " +
                "that the configuration properties are incorrectly set.")
            logger.info("[Possible Solution] 'Login through the command 'apic edit'. This " +
                "authorization method will replace this 'Login' step.")
            throw ex
        }
    }

    @Override
    public void pushApiDraft(
        String organization,
        String definition,
        String replace,
        boolean productOnly)
    {
        List<String> args = ["drafts:push", definition, "--server", server,
            "--organization", organization]

        if (replace) {
            args.addAll(["--replace", replace])
        }
        if (productOnly) {
            args.add("--product-only")
        }

        try {
            logger.info("Pushing API or definitions to drafts.")
            runCmd(args)
            logger.info("Successfully pushed definition to API Connect cloud.")
        }
        catch (ExitCodeException ex) {
            logger.error("Unable to run the 'apic drafts:push' command.")
            logger.error("[Possible Solution] Begin the process with the 'Login' step.")
            logger.error("[Possible Solution] Confirm the server, organization, and definition " +
                "properties are valid.")
            logger.error("[Possible Solution] If your properties contain spaces, surround the " +
                "properties with quotes.")
            throw ex
        }
    }

    public void publishProduct(
        String organization,
        String catalog,
        String definition,
        String space,
        boolean stage)
    {
        List<String> args = ["products:publish", definition, "--server", server,
            "--organization", organization, "--catalog", catalog]

        if (stage) {
            args.add("--stage")
        }

        if (space) {
            args.addAll(["--scope", "space", "--space", space])
        }

        try {
            logger.info("Publishing API or definitions to catalog.")
            runCmd(args)
            logger.info("Successfully published definition to API Connect cloud's catalog.")
        }
        catch (ExitCodeException ex) {
            logger.error("The 'apic publish' command failed. Review the above error for help.")
            logger.error("[Possible Solution] Begin the process with the 'Login' step.")
            logger.error("[Possible Solution] Confirm the definition file is a valid property or " +
                "it doesn't already exist.")
            logger.error("[Possible Solution] Confirm the server, organization, catalog, and " +
                "definition properties are valid.")
            logger.error("[Possible Solution] If you properties contain spaces, surround the " +
                "properties with quotes.")
            throw ex
        }
    }

    public void publishApp(File workDir, String app, String organization) {
        List<String> args = ["apps:publish", "--app", app, "--server", server,
            "--organization", organization]
        this.workDir = workDir

        try {
            logger.info("Publishing a Node.js application to a provider app.")
            runCmd(args)
            logger.info("Successfully published application to API Connect cloud's provider app.")
        }
        catch (ExitCodeException ex) {
            logger.error("The 'apic apps:publish' command failed. Review the above error for help.")
            logger.error("[Possible Solution] Begin the process with the 'Login' step.")
            logger.error("[Possible Solution] Confirm the server, organization, and app " +
                "properties are valid.")
            throw ex
        }
    }

    @Override
    public void replaceProduct(
        String oldProduct,
        String newProduct,
        List<String> plans,
        String catalog,
        String organization,
        String space)
    {
        List<String> args = ["products:replace", oldProduct, newProduct,
            "--plans", plans.join(" "), "-c", catalog, "-s", server, "-o", organization]

        if (space) {
            args.addAll(["--scope", "space", "--space", space])
        }

        try {
            logger.info("Replacing '${oldProduct}' with '${newProduct}' in Catalog.")
            runCmd(args)
            logger.info("Successfully replaced the product in API Connect.")
        }
        catch (ExitCodeException ex) {
            logger.error"The products:replace command failed. Review the above error for help."
            logger.error("[Possible Solution] Attempt to run the above apic command manually on " +
                "the agent's terminal.")
            throw ex
        }
    }

    @Override
    public void supersedeProduct(
        String oldProduct,
        String newProduct,
        List<String> plans,
        String catalog,
        String organization,
        String space)
    {
        List<String> args = ["products:supersede", oldProduct, newProduct,
            "--plans", plans.join(" "), "-c", catalog, "-s", server, "-o", organization]

        if (space) {
            args.addAll(["--scope", "space", "--space", space])
        }

        try {
            logger.info("Replacing '${oldProduct}' with '${newProduct}' in Catalog.")
            runCmd(args)
            logger.info("Successfully superseded the product in API Connect.")
        }
        catch (ExitCodeException ex) {
            logger.error"The products:supersede command failed. Review the above error for help."
            logger.error("[Possible Solution] Attempt to run the above apic command manually on " +
                "the agent's terminal.")
            throw ex
        }
    }
}
