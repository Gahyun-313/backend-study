package com.study.mvc.handler;

/**
 * HTTP 요청을 표현하는 객체
 *
 * 역할:
 * - 소켓에서 읽어들인 HTTP 요청의 핵심 정보를 담음
 * - RequestHandler가 파싱한 결과를 Router에 전달하기 위한 DTO
 *
 * 왜 필요한가?
 * - RequestHandler는 소켓 통신과 파싱을 담당
 * - Router는 비즈니스 로직(라우팅)을 담당
 * - 둘 사이에서 데이터를 주고받기 위한 객체가 필요
 *
 * 1단계와의 차이:
 * - 1단계: 파싱 결과를 변수로 흩어서 관리
 * - 2단계: HttpRequest 객체로 응집
 */
public class HttpRequest {
    private final String method;  // HTTP 메서드 (GET, POST, PUT, DELETE)
    private final String path;    // 요청 경로 (/users, /users/1)
    private final String body;    // 요청 본문 (POST, PUT에서 사용)

    /**
     * HttpRequest 생성자
     *
     * @param method HTTP 메서드
     * @param path 요청 경로
     * @param body 요청 본문 (없으면 빈 문자열)
     *
     * final 필드이므로 생성 후 변경 불가 (불변 객체)
     */
    public HttpRequest(String method, String path, String body) {
        this.method = method;
        this.path   = path;
        this.body   = body;
    }

    // === Getter 메서드 ===
    // Router에서 라우팅 조건 판단 시 사용

    public String getMethod() { return method; }
    public String getPath()   { return path; }
    public String getBody()   { return body; }

    /**
     * 로깅용 문자열 표현
     *
     * 출력 예시:
     * - GET /users
     * - POST /users | body={"name":"홍길동"}
     * - PUT /users/1 | body={"name":"김철수"}
     *
     * 왜 body가 있을 때만 표시하나?
     * - GET, DELETE는 보통 body가 없어서 로그가 간결해짐
     */
    @Override
    public String toString() {
        return method + " " + path + (body.isEmpty() ? "" : " | body=" + body);
    }
}