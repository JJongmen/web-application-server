package http;

import util.HttpRequestUtils;

public class RequestLine {
    private HttpMethod method;
    private String path;
    private String queryString;

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split(" ");
        method = HttpMethod.valueOf(tokens[0]);
        if (tokens[1].contains("?")) {
            int pos = tokens[1].indexOf("?");
            path = tokens[1].substring(0, pos);
            queryString = tokens[1].substring(pos + 1);
        } else {
            path = tokens[1];
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }
}
