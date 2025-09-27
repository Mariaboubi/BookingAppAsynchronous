package org.aueb.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Utility class for JSON operations, specifically for parsing JSON formatted strings.
 * This class uses org.json.simple library methods to convert strings into JSON objects.
 */
public class JSONUtils {
    public static JSONObject parseJSONString(String data) {
        JSONParser parser = new JSONParser(); // Create a JSON parser
        try {
            // Attempt to parse the string into a JSONObject.
            // The parse method might throw a ParseException if the string is not well-formed JSON.
            return (JSONObject) parser.parse(data);
        } catch (ParseException e) {
            System.out.println("Failed to parse request: " + e.getMessage());
            return null;
        }
    }

}
