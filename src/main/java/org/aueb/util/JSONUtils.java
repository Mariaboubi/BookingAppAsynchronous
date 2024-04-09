package org.aueb.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class JSONUtils {
    public static JSONObject parseJSONString(String data) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(data);
        } catch (ParseException e) {
            System.out.println("Failed to parse request: " + e.getMessage());
            return null;
        }
    }


}
