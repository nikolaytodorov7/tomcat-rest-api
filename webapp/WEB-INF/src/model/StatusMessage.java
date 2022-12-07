package model;

import java.util.HashMap;
import java.util.Map;

public class StatusMessage {
    private static final Map<Integer, String> CODES = new HashMap<>(Map.of(
            401, "Unauthorized", 400, "Bad Request", 403, "Forbidden"));
    public int statusCode;
    public String statusMessage;
    public String message;

    public StatusMessage(int statusCode, String message) {
        this.statusCode = statusCode;
        this.statusMessage = CODES.get(statusCode);
        this.message = message;
    }
}
