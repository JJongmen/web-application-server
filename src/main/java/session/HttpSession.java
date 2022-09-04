package session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private String id;
    private Map<String, Object> attributes = new HashMap<>();

    public HttpSession() {
        IdGenerator idGenerator = new IdGenerator();
        id = idGenerator.generateId(10);
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        attributes.clear();
    }

}
