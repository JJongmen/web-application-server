package webserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import controller.Controller;
import controller.LoginController;
import controller.SignUpController;
import controller.UserListController;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private static final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        controllerMap.put("/user/list", new UserListController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/create", new SignUpController());
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            // Start Line 읽기
            String requestUri = request.getPath();
            log.debug(request.getMethod() + " " + requestUri);

            Controller controller = controllerMap.get(requestUri);
            if (controller != null) {
                controller.service(request, response);
                return;
            }

            String contentType = request.getHeader("Accept").split(",")[0];
            contentType = "*/*".equals(contentType) ? "text/html" : contentType;
            response.forward(requestUri, contentType);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
