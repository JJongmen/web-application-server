package session;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class HttpSessionTest {

    @Test
    public void setAttribute() throws Exception {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        session.setAttribute("name", "jyp");

        assertEquals("jyp", session.getAttribute("name"));
    }

    @Test
    public void removeAttribute() throws Exception {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        session.setAttribute("name", "jyp");

        session.removeAttribute("name");

        assertNull(session.getAttribute("name"));
    }

    @Test
    public void invalidate() throws Exception {
        HttpSession session = new HttpSession(UUID.randomUUID().toString());
        session.setAttribute("name", "jyp");
        session.setAttribute("Cookie", "logined=true");

        session.invalidate();

        assertNull(session.getAttribute("name"));
        assertNull(session.getAttribute("Cookie"));
    }

}