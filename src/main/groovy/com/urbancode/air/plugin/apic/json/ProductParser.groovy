package com.urbancode.air.plugin.apic.json

import org.apache.log4j.Logger
import org.codehaus.jettison.json.JSONObject
import org.codehaus.jettison.json.JSONArray
import org.codehaus.jettison.json.JSONException

class ProductParser {
    JSONArray versions
    Logger logger

    public ProductParser(String json) {
        logger = Logger.getLogger(getClass())

        try {
            JSONObject resultObject = new JSONObject(json)
            versions = resultObject.getJSONArray("results")
        }
        catch (JSONException ex) {
            logger.error("Failed to parse products:list response: ${json}.")
            throw ex
        }
    }

    public String getVersionProperty(String version, String property) {
        String value

        for (int i = 0; i < versions.length(); i++) {
            JSONObject currVersion = versions.getJSONObject(i)

            if (currVersion.getString("version").equals(version)) {
                value = currVersion.getString(property)
                break
            }
        }

        return value
    }
}