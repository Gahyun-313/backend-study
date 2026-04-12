package com.study.http.handler;

import com.study.http.router.Router;
import com.study.http.util.JsonUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 클라이언트 소켓 1개를 처리하는 핸들러
 * Runnable 구현 -> 스레드풀에서 실행
 *
 * 처리 흐름:
 *  소켓 -> InputStrem -> 파싱 -> Router 위임 -> 응답 전송
 */
public class RequestHandler implements Runnable {

    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                InputStream  in  = socket.getInputStream();
                OutputStream out = socket.getOutputStream()
        ) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)
            );

            // 1. Request Line 파싱: "GET /users HTTP/1.1"
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            System.out.println("▶️ [Request]: " + requestLine);

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) return;

            String method = tokens[0];   // GET, POST, PUT, DELETE
            String path = tokens[1];     // /users, /users/1

            // 2. Header 파싱 (Content-Length 추출, 빈 줄 전까지)
            int contentLength = 0;
            String header;
            while ((header = reader.readLine()) != null && !header.isBlank()) {
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }

            // 3. Body 파싱 (Content-Length만큼 읽기)
            String body = "";
            if (contentLength > 0) {
                char[] buf = new char[contentLength];
                int read = reader.read(buf, 0, contentLength);
                if (read > 0) body = new String(buf, 0, read);
            }

            // 4.라우팅 & 응답
            String response;
            try {
                response = Router.route(new HttpRequest(method, path, body));
            } catch (Exception e) {
                System.err.println("[ERROR] " + e.getMessage());
                response = HttpResponse.internalError(
                        JsonUtil.errorResponse("Internal Server Error")
                );
            }

            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();

        } catch (Exception e) {
            System.err.println("❌ [SOKET Error]: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
