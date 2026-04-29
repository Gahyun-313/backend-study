package com.study.mvc.handler;

import com.study.mvc.router.Router;
import com.study.mvc.util.JsonUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * 클라이언트 요청을 처리하는 핸들러 (1개의 요청 = 1개의 스레드)
 *
 * 역할:
 * 1. Socket에서 HTTP 요청 읽기 (Request Line, Headers, Body)
 * 2. 파싱한 결과를 HttpRequest 객체로 만들기
 * 3. Router에 라우팅 위임
 * 4. 응답을 Socket으로 전송
 *
 * Runnable 구현:
 * - HttpServer에서 새 요청마다 new Thread(new RequestHandler(socket)).start()
 * - 각 요청이 독립적인 스레드에서 처리됨
 *
 * 1단계와의 차이:
 * - 1단계: RequestHandler 안에서 라우팅도 직접 처리
 * - 2단계: Router에 라우팅 로직 분리 (단일 책임 원칙)
 */
public class RequestHandler implements Runnable {

    private final Socket socket;                   // 클라이언트와 연결된 소켓
    private static final Router router = new Router();  // 라우팅 담당 객체 (정적 공유)

    /**
     * RequestHandler 생성자
     *
     * @param socket 클라이언트 연결 소켓
     *
     * HttpServer가 accept()로 받은 소켓을 전달받음
     */
    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * 스레드 실행 시 호출되는 메서드
     *
     * 전체 흐름:
     * 1. Socket에서 InputStream, OutputStream 얻기
     * 2. Request Line 파싱 (GET /users HTTP/1.1)
     * 3. Headers 파싱 (Content-Length 추출)
     * 4. Body 읽기 (Content-Length만큼)
     * 5. HttpRequest 객체 생성 → Router에 전달
     * 6. 응답을 Socket으로 전송
     * 7. 소켓 닫기
     */
    @Override
    public void run() {
        try (
                InputStream  in  = socket.getInputStream();   // 클라이언트로부터 데이터 읽기
                OutputStream out = socket.getOutputStream()   // 클라이언트로 데이터 쓰기
        ) {
            // InputStream을 문자열로 읽기 위한 Reader
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8)
            );

            // === 1. Request Line 파싱 ===
            // "GET /users HTTP/1.1" 형태
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) return;  // 빈 요청 무시

            System.out.println("[REQ] " + requestLine);  // 로깅

            // "GET /users HTTP/1.1" → ["GET", "/users", "HTTP/1.1"]
            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) return;  // 잘못된 형식 무시

            String method = tokens[0];  // GET, POST, PUT, DELETE
            String path   = tokens[1];  // /users, /users/1

            // === 2. Headers 파싱 ===
            // Content-Length 헤더를 찾아서 body 크기 파악
            int contentLength = 0;
            String header;
            while ((header = reader.readLine()) != null && !header.isBlank()) {
                // "Content-Length: 25" 형태
                if (header.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(header.split(":")[1].trim());
                }
            }
            // 빈 줄(\r\n)을 만나면 Header 끝 → Body 시작

            // === 3. Body 읽기 ===
            // Content-Length만큼만 읽음 (POST, PUT에서만 존재)
            String body = "";
            if (contentLength > 0) {
                char[] buf = new char[contentLength];  // 정확한 크기만큼 버퍼 생성
                int read = reader.read(buf, 0, contentLength);
                if (read > 0) body = new String(buf, 0, read);
            }

            // === 4. 라우팅 및 응답 생성 ===
            String response;
            try {
                // HttpRequest 객체로 만들어서 Router에 전달
                response = router.route(new HttpRequest(method, path, body));
            } catch (Exception e) {
                // 라우팅 중 예외 발생 시 500 응답
                System.err.println("[ERROR] " + e.getMessage());
                response = HttpResponse.internalError(
                        JsonUtil.errorResponse("Internal Server Error")
                );
            }

            // === 5. 응답 전송 ===
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();  // 버퍼에 남은 데이터 즉시 전송

        } catch (IOException e) {
            // 소켓 통신 오류 (클라이언트가 연결을 끊은 경우 등)
            System.err.println("[SOCKET ERROR] " + e.getMessage());
        } finally {
            // 요청 처리 완료 후 소켓 닫기 (HTTP/1.0 방식: 요청마다 연결 종료)
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}