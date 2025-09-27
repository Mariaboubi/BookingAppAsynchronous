package org.aueb.util;

import org.json.simple.JSONObject;


/**
 * The Response class encapsulates data for a single response in the system, including session identification,
 * the status of the response, an optional message, and any additional data associated with the response in a JSON format.
 */
public class Response {

    /**
     * Enum to represent possible response statuses.
     */
    public enum Status {
        SUCCESS,
        NOT_FOUND,
        UNSUCCESSFUL;
    };

    private final Long sessionId;
    private final String type;
    private final String message;
    private final JSONObject body;
    private final Status status;


    /**
     * Constructs a Response object using individual attributes.
     * @param sessionId Unique identifier for the session this response belongs to.
     * @param type The type of the response, generally reflecting the operation or request type.
     * @param status The status of the response, indicating success or specific types of failures.
     * @param message A human-readable message providing more details about the response.
     * @param body A JSONObject containing all other response-specific data.
     */
    public Response(Long sessionId,  String type, Status status, String message, JSONObject body) {
        this.sessionId = sessionId;
        this.type = type;
        this.status = status;
        this.message = message;
        this.body = body;
    }

    /**
     * Alternative constructor that parses a JSON formatted string to create a new Response object.
     * Useful for reconstructing a Response object from data received over a network.
     * @param response A string formatted as a JSON that includes all data for this response.
     */
    public Response(String response) {
        JSONObject json =  JSONUtils.parseJSONString(response);
        this.sessionId = (Long)  json.get("sessionId");
        this.type = (String) json.get("type");
        this.status = Status.valueOf((String) json.get("status"));
        this.message = (String) json.get("message");
        this.body = (JSONObject) json.get("body");
    }

    public Long getSessionId() {
        return sessionId;
    }
    public String getMessage() {
        return message;
    }
    public JSONObject getBody() {
        return body;
    }
    public Status getStatus() {
        return status;
    }

    /**
     * Converts the Response object into a JSONObject. This is useful for serialization and network transmission.
     * @return JSONObject representing this Response.
     */
    public JSONObject toJSON() {
        JSONObject response = new JSONObject();
        response.put("sessionId", sessionId);
        response.put("type",type);
        response.put("status", status.toString());
        response.put("message", message);
        response.put("body", body);
        return response;
    }

    /**
     * Static factory method to create a Response object directly from a JSON formatted string.
     * @param requestString String formatted as a JSON representing a response.
     * @return A new instance of Response initialized with the data contained in the responseString.
     */
    public static Response fromJSONString(String requestString) {
        return new Response(requestString);
    }

    /**
     * Converts this Response object to a JSON formatted string. Useful for logging and debugging purposes.
     * @return String representing this Response in JSON format.
     */
    public String toJSONString() {
        return toJSON().toJSONString();
    }

    public  String toString() {
        return "Response {" + "sessionId=" + sessionId + ", type='" + type + '\'' + ", status=" + status + ", message='" + message + '\'' + ((body != null) ? ", body=" + body.toString() : "") + '}';
    }
}
