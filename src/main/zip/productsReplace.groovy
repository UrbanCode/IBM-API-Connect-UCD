/**
 * (c) Copyright IBM Corporation 2016, 2018.
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
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

String oldProduct = props['oldProduct']?.trim()
String newProduct = props['newProduct']?.trim()
List<String> plans = props['plans']?.split("\\r?\\n")*.trim()
String catalog = props['catalog']?.trim()
String server = props['server']?.trim()
String organization = props['organization']?.trim()
String space = props['space']?.trim()
String apicPath = props['apicPath']?.trim()

APICHelper helper
try {
    helper = APICHelper.createInstance(apicPath, server)
    helper.replaceProduct(oldProduct, newProduct, plans, catalog, organization, space)
}
catch (ExitCodeException ex) {
    logger.error(ex.getMessage())
    exitCode = 1
}

System.exit(exitCode)
