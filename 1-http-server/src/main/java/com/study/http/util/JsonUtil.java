package com.study.http.util;

/**
 * JSON 파싱/생성 유틸리티 클래스
 * - Jackson ObjectMapper의 역할을 구현함으로써 JSON 파싱 이해
 */

public class JsonUtil {
    /**
     * JSON 문자열에서 특정 필드의 문자열 값을 추출
     * {"name":"홍길동"} 에서 field = "name"이면 -> "홍길동" 반환
     *
     * @param json 파싱할 JSON 문자열
     * @param field 값을 추출할 필드명
     * @return 필드에 해당하는 문자열 값, 없으면 null
     */
    public static String extractStringField(String json, String field) {
        // JSON 문자열이 null/공백이면 처리 불가
        if (json == null || json.isBlank()) return null;

        // 탐색할 키를 큰따옴표로 감싼 형태로 구성 (ex. "name")
        String key = "\"" + field + "\"";

        // JSON 내에서 키의 시작 위치 탐색
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return null;    // 키가 없으면 null 반환

        // 키 이후의 콜론(:) 위치 탐색
        int colonIndex = json.indexOf(":", keyIndex + key.length());
        if (colonIndex == -1) return null;

        // 콜론 이후의 값의 시작 큰 따옴표 위치 탐색
        int startQuote = json.indexOf("\"", colonIndex + 1);
        if (startQuote == -1) return null;

        // 시작 따옴표 이후 종료 큰 따옴표 위치 탐색
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        // 따옴표 사이의 실제 값 추출 및 앞뒤 공백 제거
        String value = json.substring(startQuote + 1, endQuote).trim();

        // 빈 문자열이면 null, 아니면 값 반환
        return value.isEmpty() ? null : value;
    }

    /**
     * 성공 응답 JSON 문자열 생성
     * {"success":true,"data":<data>}
     */
    public static String successResponse(String data) {
        return String.format("{\"success\":true,\"data\":%s}", data);
    }

    /**
     * 실패 응답 JSON 문자열 생성
     * {"success":false,"error":"<message>"}
     */
    public static String errorResponse(String message) {
        return String.format("{\"success\":false,\"error\":\"%s\"}", message);
    }
}
