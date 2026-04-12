package com.study.http.handler;

import java.nio.charset.StandardCharsets;

/**
 * HTTP 응답 문자열 빌더
 * ┌──────────────────────────────────────┐
 * │ HTTP/1.1 200 OK\r\n                  │  ← Status Line
 * │ Content-Type: application/json\r\n   │  ← Headers
 * │ Content-Length: 27\r\n               │
 * │ \r\n                                 │  ← 빈 줄
 * │ {"success":true,"data":{...}}        │  ← Body
 * └──────────────────────────────────────┘
 * \r\n = CRLF = HTTP 표준 줄 구분자
 */
public class HttpResponse {

    public static String of(int statusCode, String body) {
        String statusText = switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };

        int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

        return "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Content-Length: " + contentLength + "\r\n"
                + "\r\n"
                + body;
    }

    public static String ok(String body) {return of(200, body);}
    public static String created(String body)       { return of(201, body); }
    public static String badRequest(String body)    { return of(400, body); }
    public static String notFound(String body)      { return of(404, body); }
    public static String internalError(String body) { return of(500, body); }
}
