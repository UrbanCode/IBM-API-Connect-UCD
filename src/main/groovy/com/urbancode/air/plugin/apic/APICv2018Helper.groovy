package com.urbancode.air.plugin.apic

import com.urbancode.air.ExitCodeException
import org.apache.log4j.Logger

import com.urbancode.air.plugin.apic.json.ProductParser

class APICv2018Helper extends APICHelper {

    public APICv2018Helper(String apicPath, String server) {
        super(apicPath, server)
        this.versionCmd = "version"
        logger = Logger.getLogger(getClass())
    }

    @Override
    public void login(String username, String password, String realm, String apiKey) {
        if (apiKey) {
            logger.warn("Ignoring apiKey value '${apiKey}' since the v2018 toolkit is being used.")
        }
        List<String> args = ["login", "--server", server, "--username", username,
            "--password", password, "--realm", realm]

        try {
            logger.info("Authenticating against server '${server}' and realm '${realm} " +
                "with username '${username}'.")
            runCmd(args)
            logger.info("Authentication completed successfully.")
        }
        catch (ExitCodeException ex) {
            logger.error("Unable to run the 'apic login' command.")
            logger.error("[Possible Solution] Confirm server, username, password, and realm " +
                "properties are correct.")
            logger.error("[Possible Solution] 'Unauthorized grant type...' error may indicate " +
                "that the configuration properties are incorrectly set.")
            throw ex
        }
    }

    @Override
    public void createProduct(String prodName, String prodVersion, String prodTitle)
    {
        println("createProduct")
    }

    public void publishProduct(
        String organization,
        String catalog,
        String definition,
        String space,
        boolean stage)
    {
        List<String> args = ["products:publish", definition, "--server", server,
            "--org", organization, "--catalog", catalog]

        if (stage) {
            args.add("--stage")
        }

        if (space) {
            args.addAll(["--scope", "space", "--space", space])
        }
        else {
            args.addAll(["--scope", "catalog"])
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
            logger.error("[Possible Solution] If your properties contain spaces, surround the " +
                "properties with quotes.")
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
        List<String> configArgs = ["--server", server, "--org", organization, "--catalog", catalog]

        if (space) {
            configArgs.addAll(["--scope", "space", "--space", space])
        }
        else {
            configArgs.addAll("--scope", "catalog")
        }

        logger.info("Creating PRODUCT_PLAN_MAPPING_FILE for use in the replacement task.")
        List<String> validStates = ['published', 'deprecated']
        File migrationFile = createMigrationFile(oldProduct, plans, configArgs, validStates)
        logger.info("Successfully created mapping file '${migrationFile.absolutePath}'.")

        List<String> args = ["products:replace", newProduct, migrationFile.absolutePath]
        args.addAll(configArgs)

        try {
            logger.info("Replacing '${oldProduct}' with '${newProduct}' in Catalog.")
            runCmd(args)
            logger.info("Successfully replaced the product in API Connect.")
        }
        catch (ExitCodeException ex) {
            logger.error("The 'apic products:replace' command failed. Review the above error for " +
                "help.")
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
        List<String> configArgs = ["--server", server, "--org", organization, "--catalog", catalog]

        if (space) {
            configArgs.addAll(["--scope", "space", "--space", space])
        }
        else {
            configArgs.addAll("--scope", "catalog")
        }

        logger.info("Creating PRODUCT_PLAN_MAPPING_FILE for use in the supersede task.")
        List<String> validStates = ['published']
        File migrationFile = createMigrationFile(oldProduct, plans, configArgs, validStates)
        logger.info("Successfully created mapping file '${migrationFile.absolutePath}'.")

        List<String> args = ["products:supersede", newProduct, migrationFile.absolutePath]
        args.addAll(configArgs)

        try {
            logger.info("Superseding '${oldProduct}' with '${newProduct}' in Catalog.")
            runCmd(args)
            logger.info("Successfully superseded the product in API Connect.")
        }
        catch (ExitCodeException ex) {
            logger.error("The 'apic products:replace' command failed. Review the above error for " +
                "help.")
            logger.error("[Possible Solution] Attempt to run the above apic command manually on " +
                "the agent's terminal.")
            throw ex
        }
    }

    private File createMigrationFile(
        String oldProduct,
        List<String> plans,
        List<String> configArgs,
        List<String> validStates)
    {
        String oldProductUrl = getOldProductUrl(oldProduct, configArgs, validStates)

        File migrationFile = File.createTempFile("plans", ".yaml", workDir)
        migrationFile.deleteOnExit()

        migrationFile.withWriter{ writer ->
            writer.write("product_url: ${oldProductUrl}")
            writer.newLine()
            writer.write("plans:")
            for (String line in plans) {
                List<String> sourceTarget = line.tokenize(":")*.trim()
                if (sourceTarget.size() > 1) {
                    writer.newLine()
                    writer.write("  - source: ${sourceTarget.get(0)}")
                    writer.newLine()
                    writer.write("    target: ${sourceTarget.get(1)}")
                }
                else {
                    logger.warn("Ignoring invalid plan mapping '${line}'. Plan mapping entries " +
                        "must be in the following format 'SOURCE_PLAN_NAME:TARGET_PLAN_NAME'.")
                }
            }
        }

        return migrationFile
    }

    private String getOldProductUrl(
        String oldProduct,
        List<String> configArgs,
        List<String> validStates)
    {
        List<String> productVersionPair = oldProduct.tokenize(":")*.trim()

        String productName = productVersionPair.get(0)
        String version = ""

        if (productVersionPair.size() < 2) {
            logger.info("A specific version wasn't supplied in the product to be " +
                "replaced/superseded. The earliest published version will be used instead.")
        }
        else {
            version = productVersionPair.get(1)
        }

        List<String> args = ["products:list", productName, "--format", "json"]
        args.addAll(configArgs)

        String json = runCmd(args, true)
        ProductParser parser = new ProductParser(json)

        if (parser.isVersionsEmpty()) {
            throw new ExitCodeException("The product '${oldProduct}' doesn't have any versions, " +
                "or the versions are not in any of the following states: ${validStates}.")
        }

        return parser.getVersionProperty(version, "url", validStates)
    }
}
