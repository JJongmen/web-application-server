package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            // Start Line 읽기
            String startLine = br.readLine();
            String requestUri = parseUri(startLine);
            String httpMethod = parseMethod(startLine);
            log.debug("Request URI = {}", requestUri);
            log.debug("HTTP Method = {}", httpMethod);

            // HTTP Header 읽기
            int contentLength = getContentLength(br);

            // HTTP Request Body 읽기
            String formData = null;
            if (httpMethod.equals("POST")) {
                formData = getFormData(br, contentLength);
            }
            
            // 회원가입 요청
            if (requestUri.equals("/user/create")) {
                signUp(out, formData);
                return;
            }

            DataOutputStream dos = new DataOutputStream(out);
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

    private int getContentLength(BufferedReader br) throws IOException {
        String line;
        int contentLength = 0;
        while (!"".equals(line = br.readLine())) {
            Pair headerPair = parseHeader(line);
            if (headerPair.getKey().equals("Content-Length")) {
                contentLength = Integer.parseInt(headerPair.getValue());
                log.debug("Content-Length = {}", contentLength);
            }
        }
        return contentLength;
    }

    private String getFormData(BufferedReader br, int contentLength) throws IOException {
        String formData;
        formData = readData(br, contentLength);
        log.debug("formData = {}", formData);
        return formData;
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
