package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.HttpRequestUtils.*;

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

            // Start Line 읽기
            String requestUri = request.getPath();
            String httpMethod = request.getMethod();
            log.debug(httpMethod + " " + requestUri);

            // 회원가입 요청
            if (requestUri.equals("/user/create") && httpMethod.equals("POST")) {
                signUp(request, response);
                return;
            }

            // 로그인 요청
            if (requestUri.equals("/user/login") && httpMethod.equals("POST")) {
                login(request, response);
                return;
            }

            // 사용자 목록 요청
            if (requestUri.equals("/user/list") && httpMethod.equals("GET")) {
                userList(request, response);
                return;
            }

            String contentType = request.getHeader("Accept").split(",")[0];
            contentType = "*/*".equals(contentType) ? "text/html" : contentType;
            response.forward(requestUri, contentType);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static void userList(HttpRequest request, HttpResponse response) {
        if (!isLogined(request)) {
            response.sendRedirect("/user/login.html");
            return;
        }
        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }


    private static boolean isLogined(HttpRequest request) {
        return Boolean.parseBoolean(parseCookies(request.getHeader("Cookie")).getOrDefault("logined", "false"));
    }



    private void login(HttpRequest request, HttpResponse response) {
        User findUser = DataBase.findUserById(request.getParameter("userId"));
        if (correctLoginInfo(request, findUser)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
            return;
        }
        response.sendRedirect("/user/login_failed.html");
    }

    private static boolean correctLoginInfo(HttpRequest request, User findUser) {
        return findUser != null && findUser.getPassword().equals(request.getParameter("password"));
    }

    private void signUp(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"), request.getParameter("email"));
        DataBase.addUser(user);
        log.debug("Add User to DataBase : {}", user);

        response.sendRedirect("/index.html");
    }
}
