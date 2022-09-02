package http;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestLineTest {
    @Test
    public void requestLine_with_queryString() throws Exception {
        String line = "GET /user/create?userId=jjongmen&password=password&name=jyp HTTP/1.1";
        RequestLine requestLine= new RequestLine(line);
        assertEquals(HttpMethod.GET, requestLine.getMethod());
        assertEquals("/user/create", requestLine.getPath());
        assertEquals("userId=jjongmen&password=password&name=jyp", requestLine.getQueryString());
    }

}