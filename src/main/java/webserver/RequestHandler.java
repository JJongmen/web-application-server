package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import db.DataBase;
import message.HttpRequestMessage;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static util.HttpRequestUtils.*;
import static util.IOUtils.*;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private static final Map<Integer, String> statusCodeMap = new HashMap<>();
    static {
        statusCodeMap.put(200, "OK");
        statusCodeMap.put(302, "Found");
        statusCodeMap.put(400, "Bad Request");
    }

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequestMessage requestMessage = new HttpRequestMessage(in);
            DataOutputStream dos = new DataOutputStream(out);

            // Start Line 읽기
            String requestUri = requestMessage.getUri();
            String httpMethod = requestMessage.getMethod();
            log.debug(httpMethod + " " + requestUri);

            String formData = requestMessage.getBody();
            log.debug("body={}",formData);

            // 회원가입 요청
            if (requestUri.equals("/user/create") && httpMethod.equals("POST")) {
                signUp(out, formData);
                return;
            }

            // 로그인 요청
            if (requestUri.equals("/user/login") && httpMethod.equals("POST")) {
                login(formData, dos);
                return;
            }

            try {
                byte[] body = Files.readAllBytes(getFilePath(requestUri));
                response200Header(dos, body.length);
                responseBody(dos, body);
            } catch (Exception e) {
                response404Header(dos);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void login(String formData, DataOutputStream dos) {
        Map<String, String> parameters = parseQueryString(formData);
        User findUser = DataBase.findUserById(parameters.get("userId"));
        responseLoginHeader(dos, correctLoginInfo(parameters, findUser));
    }

    private static boolean correctLoginInfo(Map<String, String> parameters, User findUser) {
        return findUser != null && findUser.getPassword().equals(parameters.get("password"));
    }

    private void signUp(OutputStream out, String formData) {
        Map<String, String> parameters = parseQueryString(formData);
        User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email"));
        DataBase.addUser(user);
        log.debug("Add User to DataBase : {}", user);

        DataOutputStream dos = new DataOutputStream(out);
        String newUrl = "/index.html";
        response302Header(dos, newUrl);
        log.debug("Redirecting to {}", newUrl);
    }

    private static Path getFilePath(String requestUri) {
        return new File("./webapp" + requestUri).toPath();
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String newUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + newUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseLoginHeader(DataOutputStream dos, boolean success) {
        String newUrl = success ? "/index.html" : "/user/login_failed.html";
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + newUrl + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + success + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
