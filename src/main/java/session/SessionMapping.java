package session;

import java.util.HashMap;
import java.util.Map;

public final class SessionMapping {
    private static Map<String, HttpSession> sessions = new HashMap<>();

    public static HttpSession getSession(String sessionId) {
        HttpSession session = sessions.get(sessionId);
        return session;
    }

    public static HttpSession createSession() {
        HttpSession session = new HttpSession();
        sessions.put(session.getId(), session);
        return session;
    }
}
