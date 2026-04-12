package com.study.http.handler;

/**
 * 파싱된 HTTP 요청을 담는 구조체
 * - HttpServletRequest의 역할
 *
 * HTTP 요청 구조:
 * ┌──────────────────────────────┐
 * │ GET /users HTTP/1.1          │  ← Request Line
 * │ Host: localhost:8080         │  ← Headers
 * │ Content-Length: 20           │
 * │                              │  ← 빈 줄 (헤더/바디 구분)
 * │ {"name":"홍길동"}             │  ← Body
 * └──────────────────────────────┘
 */
public class HttpRequest {
    private final String method;
    private final String path;
    private final String body;

    public HttpRequest(String method, String path, String body) {
        this.method = method;
        this.path = path;
        this.body = body;
    }

    public String getMethod() {return method;}
    public String getPath() {return path;}
    public String getBody() {return body;}

    @Override
    public String toString() {
        return method + " " + path + (body.isEmpty() ? "" : " | body= " + body);
    }
}
