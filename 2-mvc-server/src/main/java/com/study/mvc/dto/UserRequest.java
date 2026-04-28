package com.study.mvc.dto;

/**
 * 클라이언트 → 서버 요청 데이터
 *
 * 학습 포인트:
 * 1단계에서는 Controller가 body 문자열을 직접 파싱했다.
 * 2단계에서는 요청 데이터를 구조체(DTO)로 먼저 변환 후 사용한다.
 * → 실무에서는 Jackson이 JSON → DTO 변환을 자동으로 처리한다.
 */

public class UserRequest {
    private final String name;

    public UserRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public boolean isValid() {
        return name != null && !name.isBlank();
    }
}