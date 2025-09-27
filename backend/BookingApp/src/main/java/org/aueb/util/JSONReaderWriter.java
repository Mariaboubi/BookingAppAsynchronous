package org.aueb.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;

/**
 * Utility class to handle JSON read and write operations. This class provides methods to read from
 * and write to JSON files using simple JSON parsing.
 */
public class JSONReaderWriter {

    /**
     * Reads a JSON object from a file.
     *
     * @param filePath the path of the file to read the JSON object from.
     * @return A JSONObject parsed from the file.
     * @throws Exception if there is an error in reading the file or parsing the JSON data.
     * This might include IOException if the file can't be opened, or a ParseException if the JSON data is malformed.
     */
    public static JSONObject readJsonFile(String filePath) throws Exception {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(filePath));
    }

    /**
     * Writes a given JSON object to a file.
     *
     * @param jsonObject the JSON object to write to the file.
     * @param filePath the path of the file where the JSON object should be written.
     * @throws Exception if there is an error writing the JSON data to the file.
     * This encapsulates any IOExceptions that may occur due to file write operations.
     */
    public static void writeJsonFile(JSONObject jsonObject, String filePath) throws Exception {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toJSONString());
            file.flush();
        }
    }

}
