package com.study.spring.exception;

import lombok.Getter;

/**
 * 학습 포인트:
 * 공통 에러 응답 형식
 * {"success": false, "error": "..."}
 *
 * 2단계: JsonUtil.errorResponse()로 문자열 직접 조립
 * 3단계: 객체로 정의하면 Jackson이 자동으로 JSON 변환
 */
@Getter
public class ErrorResponse {

    private final boolean success = false;
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}