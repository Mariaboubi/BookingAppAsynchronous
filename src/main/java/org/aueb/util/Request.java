package org.aueb.util;

import org.json.simple.JSONObject;

/**
 * The Request class encapsulates data for a single request in the system, including session identification,
 * the type of request, and any additional data associated with the request in a JSON format.
 */
public class Request {
    private final Long sessionId; // unique session id
    private final String type; // type of request
    private final JSONObject body; // additional data associated with the request

    public Long getSessionId() {
        return sessionId;
    }

    public String getType() {
        return type;
    }

    public JSONObject getBody() {
        return body;
    }

    /**
     * Constructs a new Request with a session id, a type, and a JSON object as the body.
     * @param sessionId The unique session id associated with the request.
     * @param type The type of request.
     * @param body The additional data associated with the request in a JSON format.
     */
    public Request(Long sessionId, String type, JSONObject body) {
        this.sessionId = sessionId;
        this.type = type;
        this.body = body;
    }

    /**
     * Constructs a new Request from a JSON formatted string.
     * @param request The JSON formatted string that represents the request.
     */
    public Request(String request) {
        JSONObject json =  JSONUtils.parseJSONString(request);
        this.sessionId = (Long)  json.get("sessionId");
        this.type = (String) json.get("type");
        this.body = (JSONObject) json.get("body");
    }

    /**
     * Converts the Request object back into a JSONObject.
     * @return JSONObject representing this Request.
     */
    public JSONObject toJSON() {
        JSONObject response = new JSONObject();
        response.put("sessionId", sessionId);
        response.put("type",type);
        response.put("body", body);
        return response;
    }

    /**
     * Static factory method to create a Request object directly from a JSON formatted string.
     * @param requestString String formatted as a JSON representing a request.
     * @return A new instance of Request initialized with the data contained in the requestString.
     */
    public static Request fromJSONString(String requestString) {
        return new Request(requestString);
    }

    /**
     * Converts this Request object to a JSON formatted string. Useful for logging and debugging purposes.
     * @return String representing this Request in JSON format.
     */
    public String toJSONString() {
        return toJSON().toJSONString();
    }

    /**
     * Provides a string representation of this Request object.
     * @return A string representation of this Request.
     */
    public String toString() {
        return "Request {" + "sessionId=" + sessionId + ", type='" + type + '\'' + ((body != null) ? ", body=" + body.toString() : "") + '}';
    }
}
