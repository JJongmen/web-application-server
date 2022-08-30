package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String requestUri = HttpRequestUtils.parseUri(br.readLine());
            log.debug("Request URI = {}", requestUri);
//            String line;
//            while (!"".equals(line = br.readLine()) && line != null) {
//                log.debug(line);
//            }
            if (requestUri.startsWith("/user/create?")) {
                String queryString = HttpRequestUtils.getQueryString(requestUri);
                Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email"));
                log.debug("user = {}", user);
            }

            byte[] body = null;
            DataOutputStream dos = new DataOutputStream(out);
            body = Files.readAllBytes(new File("./webapp" + requestUri).toPath());
            try {
                response200Header(dos, body.length);
            } catch (NullPointerException e) {
                log.debug("Client Request URI : {}", requestUri);
                log.debug("Response Bad Request");
                body = "Invalid URL. Please try again".getBytes();
                response400Header(dos, body.length);
            }
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void response400Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 400 BAD REQUEST \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
