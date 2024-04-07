package org.aueb.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class JSONUtils {
    private static JSONObject parseJSONString(String data) {
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(data);
        } catch (ParseException e) {
            System.out.println("Failed to parse request: " + e.getMessage());
            return null;
        }
    }
        public static JSONObject receiveJson(InputStreamReader streamReader) {
            StringBuilder jsonBuilder = new StringBuilder();
            try {
                // Create a BufferedReader to read the data from the socket's input stream
                BufferedReader reader = new BufferedReader(streamReader);
                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println("line "+line);
                    jsonBuilder.append(line);
                }
                System.out.println("end line "+line);
                // Assuming the sender closes the connection, which ends the read loop
            } catch (Exception e) {
                throw new RuntimeException("Error reading JSON from socket", e);
            }

            // Convert the StringBuilder content to a JSONObject
            return parseJSONString(jsonBuilder.toString());
        }

}
