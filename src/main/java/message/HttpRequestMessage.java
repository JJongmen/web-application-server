package message;

import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.*;

public class HttpRequestMessage {

    private StartLine startLine;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpRequestMessage() {}

    public HttpRequestMessage(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            startLine = new StartLine(br.readLine());

            String line;
            while (!"".equals(line = br.readLine()) && line != null) {
                Pair headerPair = parseHeader(line);
                headers.put(headerPair.getKey(), headerPair.getValue());
            }

            body = IOUtils.readData(br, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private class StartLine {
        private String method;
        private String uri;
        private String version;

        public StartLine(String startLine) {
            String[] tokens = startLine.split(" ");
            method = tokens[0];
            uri = tokens[1];
            version = tokens[2];
        }

        public String getMethod() {
            return method;
        }

        public String getUri() {
            return uri;
        }

        public String getVersion() {
            return version;
        }
    }
}
