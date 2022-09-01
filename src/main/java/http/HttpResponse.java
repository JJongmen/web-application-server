package http;

import org.slf4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class HttpResponse {
    private final Logger log = getLogger(HttpResponse.class);
    private DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();


    public HttpResponse(OutputStream out) {
        dos = new DataOutputStream(out);
    }

    public void forward(String url) {
        try {
            byte[] body = Files.readAllBytes(getFilePath(url));
            response200Header(body.length);
            responseBody(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forward(String url, String contentType) {
        addHeader("Content-Type", contentType + ";charset=utf-8");
        forward(url);
    }

    public void sendRedirect(String newUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            addHeader("Location", newUrl);
            processHeaders();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addHeader(String header, String value) {
        if (headers.containsKey(header)) {
            headers.put(header, headers.get(header) + "," + value);
            return;
        }
        headers.put(header, value);
    }

    private static Path getFilePath(String requestUri) {
        StringBuilder sb = new StringBuilder("./webapp");
        sb.append(requestUri);
        return new File(sb.toString()).toPath();
    }

    private void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            addHeader("Content-Length", lengthOfBodyContent + "");
            processHeaders();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processHeaders() throws IOException {
        for (String header : headers.keySet()) {
            dos.writeBytes(header + ": " + headers.get(header) + " \r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body) {
        try {
            byte[] content = body.getBytes();
            response200Header(content.length);
            dos.write(content, 0, content.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
