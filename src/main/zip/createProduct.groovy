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
import com.urbancode.air.ExitCodeException
import com.urbancode.air.plugin.apic.APICHelper
import com.urbancode.air.plugin.apic.APICv2018Helper
import com.urbancode.air.plugin.apic.APICv10Helper
import org.apache.log4j.Logger
import org.apache.log4j.Level

AirPluginTool apTool = new AirPluginTool(this.args[0], this.args[1])

Properties props = apTool.getStepProperties()
int exitCode = 0

String logLevel = props['loggerLevel']
Logger.getRootLogger().setLevel(Level.toLevel(logLevel, Level.INFO))
Logger logger = Logger.getLogger(getClass())

String apicPath = props['apicPath']?.trim()
String prodName = props['prodName']?.trim()
String server = props['server']?.trim()

String prodVersion = props['prodVersion']?.trim()
String prodTitle = props['prodTitle']?.trim()

APICHelper helper
try {
    helper = APICHelper.createInstance(apicPath, server)
    helper.createProduct(prodName, prodVersion, prodTitle)
}
catch (ExitCodeException ex) {
    logger.error(ex.getMessage())
    exitCode = 1
}

System.exit(exitCode)
