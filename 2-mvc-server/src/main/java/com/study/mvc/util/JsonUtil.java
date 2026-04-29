package com.study.mvc.util;

/**
 * JSON 문자열 파싱 및 생성 유틸리티
 *
 * 역할:
 * 1. JSON 문자열에서 특정 필드 값 추출 (extractStringField)
 * 2. 성공/실패 응답 JSON 생성 (successResponse, errorResponse)
 *
 * 학습 포인트:
 * - 1~2단계는 Jackson 같은 라이브러리 없이 수동으로 JSON 처리
 * - 3단계(Spring)에서는 Jackson이 자동으로 처리
 *
 * 주의사항:
 * - 매우 단순한 파싱 방식 (중첩 객체, 배열, 이스케이프 처리 안 됨)
 * - 실무에서는 절대 이렇게 안 함 (보안, 유지보수 문제)
 * - 학습 목적으로만 사용
 */
public class JsonUtil {

    /**
     * JSON 문자열에서 특정 필드의 값을 추출
     *
     * @param json JSON 문자열 (예: {"name":"홍길동","age":30})
     * @param field 추출할 필드명 (예: "name")
     * @return 추출된 값 (예: "홍길동") / 없으면 null
     *
     * 처리 과정:
     * 1. "name" 키 찾기
     * 2. : 찾기
     * 3. : 뒤의 첫 번째 " 찾기 (값 시작)
     * 4. 값 시작 뒤의 " 찾기 (값 끝)
     * 5. 시작과 끝 사이의 문자열 추출
     *
     * 예시:
     * json = {"name":"홍길동"}
     * field = "name"
     *
     * 단계별 인덱스:
     * - keyIdx = 1 ("name" 시작 위치)
     * - colonIdx = 7 (: 위치)
     * - startQuote = 8 (값 앞 " 위치)
     * - endQuote = 11 (값 뒤 " 위치)
     * - substring(9, 11) = "홍길동"
     *
     * 한계점:
     * - 중첩 객체 처리 불가: {"user":{"name":"홍길동"}}
     * - 배열 처리 불가: {"names":["홍길동","김철수"]}
     * - 이스케이프 처리 불가: {"text":"He said \"Hello\""}
     * - 숫자/불리언은 추출 불가 (문자열만 가능)
     *
     * 왜 이렇게 만들었나?
     * - HTTP body에서 name 필드만 추출하면 되는 간단한 상황
     * - Jackson 없이 직접 파싱 로직 구현 경험
     * - 실무에서는 ObjectMapper.readValue() 사용
     */
    public static String extractStringField(String json, String field) {
        // null 체크
        if (json == null || json.isBlank()) return null;

        // "name" 형태로 키 구성
        String key = "\"" + field + "\"";
        int keyIdx = json.indexOf(key);
        if (keyIdx == -1) return null;  // 키가 없으면 null

        // : 찾기
        int colonIdx = json.indexOf(":", keyIdx + key.length());
        if (colonIdx == -1) return null;

        // 값 시작 " 찾기
        int startQuote = json.indexOf("\"", colonIdx + 1);
        if (startQuote == -1) return null;

        // 값 끝 " 찾기
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        // 값 추출 및 공백 제거
        String value = json.substring(startQuote + 1, endQuote).trim();
        return value.isEmpty() ? null : value;  // 빈 문자열이면 null 반환
    }

    /**
     * 성공 응답 JSON 생성
     *
     * @param data 응답 데이터 (이미 JSON 형태여야 함)
     * @return {"success":true,"data":...} 형태의 JSON
     *
     * 사용 예시:
     * String userJson = "{\"id\":1,\"name\":\"홍길동\"}";
     * successResponse(userJson)
     * → {"success":true,"data":{"id":1,"name":"홍길동"}}
     *
     * 주의:
     * - data는 이미 JSON 문자열이어야 함 (따옴표로 감싸지 않음)
     * - UserResponse.toJson()이 반환한 결과를 그대로 넣음
     *
     * 왜 이런 형식인가?
     * - 클라이언트가 성공/실패를 success 필드로 판단 가능
     * - 실제 데이터는 data 필드에 담김
     * - 일관된 응답 형식 유지
     */
    public static String successResponse(String data) {
        return String.format("{\"success\":true,\"data\":%s}", data);
    }

    /**
     * 실패 응답 JSON 생성
     *
     * @param message 에러 메시지
     * @return {"success":false,"error":"..."} 형태의 JSON
     *
     * 사용 예시:
     * errorResponse("유저를 찾을 수 없습니다")
     * → {"success":false,"error":"유저를 찾을 수 없습니다"}
     *
     * successResponse와의 차이:
     * - success: false
     * - data 대신 error 필드 사용
     * - message는 문자열이므로 따옴표로 감쌈
     *
     * 클라이언트 사용 예시 (JavaScript):
     * fetch('/users/999')
     *   .then(res => res.json())
     *   .then(data => {
     *     if (data.success) {
     *       console.log(data.data);  // 성공 시
     *     } else {
     *       console.error(data.error);  // 실패 시
     *     }
     *   });
     */
    public static String errorResponse(String message) {
        return String.format("{\"success\":false,\"error\":\"%s\"}", message);
    }
}