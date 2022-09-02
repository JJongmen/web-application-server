package http;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestParamsTest {
    @Test
    public void addQueryString() throws Exception {
        RequestParams requestParams = new RequestParams();
        requestParams.addQueryString("userId=jjongmen&password=password&name=jyp");

        assertEquals("jjongmen", requestParams.getParameter("userId"));
        assertEquals("password", requestParams.getParameter("password"));
        assertEquals("jyp", requestParams.getParameter("name"));
    }
}