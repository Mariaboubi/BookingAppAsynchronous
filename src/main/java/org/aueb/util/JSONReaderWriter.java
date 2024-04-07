package org.aueb.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

public class JSONReaderWriter {

    public static final String JSON_FILE_PATH = "bin/hotel.json";
    public static JSONObject readJsonFile(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(filePath));
    }

    public static void writeJsonFile(JSONObject jsonObject, String filePath) throws Exception {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toJSONString());
            file.flush();
        }
    }
}
