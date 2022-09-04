package webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import session.HttpSession;
import session.SessionMapping;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String path = getDefaultPath(request.getPath());
            log.debug(request.getMethod() + " " + path);

            processSession(request, response);

            Controller controller = RequestMapping.getController(path);
            if (controller != null) {
                controller.service(request, response);
                return;
            }

            response.forward(path);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static void processSession(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie("Session-Id");
        HttpSession session;
        log.debug("sessionId : {}", sessionId);
        if (sessionId == null) {
            session = SessionMapping.createSession();
            response.addCookie("Session-Id", session.getId());
        } else {
            session = SessionMapping.getSession(sessionId);
        }
        request.setSession(session);
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
