package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {
    private Map<String, String> cookies;

    HttpCookie(String cookieLine) {
        cookies = HttpRequestUtils.parseCookies(cookieLine);
    }

    String getCookie(String key) {
        return cookies.get(key);
    }
}
