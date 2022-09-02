package http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final String CONTENT_LENGTH = "Content-Length";
    private Map<String, String> headers = new HashMap<>();

    void parseHeader(String line) {
        String[] tokens = line.split(":");
        headers.put(tokens[0].trim(), tokens[1].trim());
    }

    String getHeader(String name) {
        return headers.get(name);
    }

    int getIntHeader(String name) {
        String header = headers.get(name);
        if (header == null || header.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(header);
    }

    int getContentLength() {
        return getIntHeader(CONTENT_LENGTH);
    }
}
