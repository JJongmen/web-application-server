package http;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static util.HttpRequestUtils.*;

public class HttpRequest {

    private StartLine startLine;
    private Map<String, String> headers = new HashMap<>();
    private String body;
    private Map<String, String> parameters;

    private HttpRequest() {}

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            readStartLine(br);
            readHeaders(br);
            readBody(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readBody(BufferedReader br) throws IOException {
        body = IOUtils.readData(br, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));
        if (parameters.isEmpty()) {
            parameters = parseQueryString(body);
        }
    }

    private void readStartLine(BufferedReader br) throws IOException {
        startLine = new StartLine(br.readLine());
        parameters = parseQueryString(startLine.getQueryString());
    }

    private void readHeaders(BufferedReader br) throws IOException {
        String line;
        while (!"".equals(line = br.readLine()) && line != null) {
            Pair headerPair = parseHeader(line);
            headers.put(headerPair.getKey(), headerPair.getValue());
        }
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }

    public String getHeader(String header) {
        return headers.get(header);
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

        public String getPath() {
            if (uri.contains("?")) {
                return uri.substring(0, uri.indexOf("?"));
            }
            return uri;
        }

        public String getQueryString() {
            if (uri.contains("?")) {
                return HttpRequestUtils.getQueryString(uri);
            }
            return "";
        }

        public String getVersion() {
            return version;
        }
    }
}
