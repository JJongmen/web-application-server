package http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestParams {
    private Map<String, String> parameters = new HashMap<>();

    void addQueryString(String queryString) {
        putParams(queryString);
    }

    void addBody(String body) {
        putParams(body);
    }

    String getParameter(String name) {
        return parameters.get(name);
    }

    private void putParams(String data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        parameters.putAll(HttpRequestUtils.parseQueryString(data));
    }
}
