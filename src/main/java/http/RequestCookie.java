package http;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestCookie {
    private Map<String, String> cookies;

    String getCookie(String key) {
        return cookies.get(key);
    }

    void processCookies(String cookieLine) {
        cookies = HttpRequestUtils.parseCookies(cookieLine);
    }
}
