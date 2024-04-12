package org.aueb.util;

import org.json.simple.JSONObject;

public class Response {
    public enum Status {
        SUCCESS,
        NOT_FOUND;
    };

    /* Create a JSONObject encapsulating the status of the request, a message and an optional result */
    public static JSONObject create(String type,Status status, String message, JSONObject body) {
        JSONObject response = new JSONObject();
        response.put("type",type);
        response.put("status", status.name());
        response.put("message", message);
        response.put("body", body);
        return response;
    }

    /* Create a JSONObject from a string */
    public static JSONObject fromString(String responseString) {
        return JSONUtils.parseJSONString(responseString);
    }
}