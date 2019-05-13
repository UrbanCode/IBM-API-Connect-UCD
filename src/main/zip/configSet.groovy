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
import org.apache.log4j.Logger
import org.apache.log4j.Level

AirPluginTool apTool = new AirPluginTool(this.args[0], this.args[1])

Properties props = apTool.getStepProperties()
int exitCode = 0

String logLevel = props['loggerLevel']
Logger.getRootLogger().setLevel(Level.toLevel(logLevel, Level.INFO))
Logger logger = Logger.getLogger(getClass())

String server = props['server']?.trim()
String organization = props['organization']?.trim()
String type = props['type']?.trim()
String name = props['name']?.trim()
String apicPath = props['apicPath']?.trim()
boolean global = Boolean.valueOf(props['global'])
boolean local = Boolean.valueOf(props['local'])

APICHelper helper
try {
    helper = APICHelper.createInstance(apicPath, server)
    helper.setConfigVariable(organization, type, name, global, local)
}
catch (ExitCodeException ex) {
    logger.error(ex.getMessage())
    exitCode = 1
}

System.exit(exitCode)
