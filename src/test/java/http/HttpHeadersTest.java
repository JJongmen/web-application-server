package http;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpHeadersTest {
    @Test
    public void addHeader() throws Exception {
        HttpHeaders headers = new HttpHeaders();

        headers.parseHeader("Set-Cookie: logined=true");

        assertEquals("logined=true", headers.getHeader("Set-Cookie"));
    }

    @Test
    public void getContentLength() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.parseHeader("Content-Length: 100");

        assertEquals(100, headers.getContentLength());
    }

}