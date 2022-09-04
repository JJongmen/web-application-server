package http;

import session.HttpSession;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private HttpHeaders headers = new HttpHeaders();
    private RequestParams parameters = new RequestParams();
    private HttpSession session;

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            readRequestLine(br);
            readHeaders(br);
            parameters.addBody(IOUtils.readData(br, headers.getContentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHeaders(BufferedReader br) throws IOException {
        String line;
        while (!"".equals(line = br.readLine()) && line != null) {
            headers.parseHeader(line);
        }
        headers.processCookie();
    }

    private void readRequestLine(BufferedReader br) throws IOException {
        requestLine = new RequestLine(br.readLine());
        parameters.addQueryString(requestLine.getQueryString());
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String name) {
        return parameters.getParameter(name);
    }

    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    public String getCookie(String name) {
        return headers.getCookie(name);
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public HttpSession getSession() {
        return session;
    }
}
