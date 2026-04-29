package com.study.mvc.handler;

import java.nio.charset.StandardCharsets;

/**
 * HTTP 응답 메시지를 생성하는 유틸리티 클래스
 *
 * 역할:
 * - HTTP 응답 형식에 맞는 문자열 생성
 * - Status Line + Headers + Body를 조립
 *
 * HTTP 응답 형식:
 * HTTP/1.1 200 OK\r\n
 * Content-Type: application/json; charset=UTF-8\r\n
 * Content-Length: 42\r\n
 * \r\n
 * {"status":"success","data":{"id":1}}
 *
 * 학습 포인트:
 * - 1단계에서도 동일하게 사용
 * - Controller가 이 클래스를 사용해서 응답 문자열을 만듦
 */
public class HttpResponse {

    /**
     * HTTP 응답 메시지 생성 (핵심 메서드)
     *
     * @param statusCode HTTP 상태 코드 (200, 201, 400, 404, 500)
     * @param body 응답 본문 (JSON 문자열)
     * @return 완전한 HTTP 응답 메시지
     *
     * 처리 과정:
     * 1. statusCode → statusText 변환 (200 → "OK")
     * 2. body의 바이트 길이 계산 (UTF-8 기준)
     * 3. Status Line + Headers + Body 조립
     *
     * 왜 Content-Length를 계산하나?
     * - HTTP/1.1 스펙상 body 크기를 명시해야 함
     * - 클라이언트가 응답을 정확히 읽을 수 있도록
     * - 한글은 UTF-8로 3바이트이므로 String.length()가 아닌 byte 길이 계산 필요
     */
    public static String of(int statusCode, String body) {
        // 상태 코드에 해당하는 설명 문구
        String statusText = switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default  -> "Unknown";
        };

        // body를 UTF-8로 인코딩했을 때의 바이트 길이
        int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

        // HTTP 응답 메시지 조립
        // \r\n: HTTP 스펙상 줄바꿈은 CRLF (Carriage Return + Line Feed)
        // 빈 줄(\r\n): Header와 Body 구분자
        return "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: application/json; charset=UTF-8\r\n"
                + "Content-Length: " + contentLength + "\r\n"
                + "\r\n"
                + body;
    }

    /**
     * 편의 메서드들 - 자주 쓰는 상태 코드를 메서드로 제공
     *
     * 사용 예시:
     * - return HttpResponse.ok(successBody);
     * - return HttpResponse.notFound(errorBody);
     *
     * 왜 이렇게 만들었나?
     * - of(200, body)보다 ok(body)가 의도가 명확
     * - Controller 코드가 읽기 쉬워짐
     */
    public static String ok(String body)            { return of(200, body); }
    public static String created(String body)       { return of(201, body); }
    public static String badRequest(String body)    { return of(400, body); }
    public static String notFound(String body)      { return of(404, body); }
    public static String internalError(String body) { return of(500, body); }
}