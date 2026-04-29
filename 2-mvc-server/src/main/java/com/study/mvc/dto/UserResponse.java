package com.study.mvc.dto;

import com.study.mvc.domain.User;
import java.time.format.DateTimeFormatter;

/**
 * 서버 → 클라이언트로 나가는 응답 데이터를 담는 DTO
 *
 * 학습 포인트:
 * - 도메인 객체(User)를 그대로 반환하지 않고 DTO로 변환해서 반환
 * - 도메인 내부 구조가 바뀌어도 API 응답 형식을 유지할 수 있음
 *
 * 왜 도메인 객체를 직접 반환하면 안 되나?
 * 1. 불필요한 정보 노출 방지
 *    - User에 password 필드가 추가되면 자동으로 응답에 포함될 수 있음
 * 2. API 스펙 독립성
 *    - User 내부 필드명이 바뀌어도 UserResponse만 수정하면 됨
 * 3. 표현 형식 제어
 *    - LocalDateTime을 원하는 문자열 형식으로 변환
 *
 * 실무에서는?
 * - @JsonProperty("user_id")로 필드명을 클라이언트 요구사항에 맞게 제어
 * - @JsonIgnore로 특정 필드 응답에서 제외
 */
public class UserResponse {
    private final Long id;           // 사용자 고유 ID
    private final String name;       // 사용자 이름
    private final String createdAt;  // 생성 시각 (문자열로 변환됨)

    /**
     * private 생성자
     *
     * 왜 private인가?
     * - 외부에서 new UserResponse()로 직접 생성하지 못하게 막음
     * - 오직 from() 메서드를 통해서만 생성 가능
     * - 생성 로직을 한 곳에 집중시켜 일관성 유지
     */
    private UserResponse(Long id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * 도메인 객체 → DTO 변환 (정적 팩토리 메서드)
     *
     * @param user 변환할 도메인 객체
     * @return UserResponse DTO
     *
     * 사용 예시:
     * - UserResponse response = UserResponse.from(user);
     *
     * 왜 정적 팩토리 메서드를 사용하나?
     * 1. 명확한 의도 표현: from()이라는 이름으로 "변환"임을 명시
     * 2. 날짜 포맷팅 로직을 한 곳에 모음
     * 3. 생성자보다 유연 (필요시 캐싱, 검증 등 추가 가능)
     *
     * LocalDateTime → String 변환:
     * - "2024-01-15T14:30:00" 형태의 ISO-8601 포맷
     * - 클라이언트가 파싱하기 쉬운 표준 형식
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                )
        );
    }

    // === Getter 메서드 ===
    // Controller에서 JSON 변환 시 사용

    public Long getId() { return id; }
    public String getName()   { return name; }
    public String getCreatedAt() { return createdAt; }

    /**
     * DTO를 JSON 문자열로 변환
     *
     * 생성 결과 예시:
     * {"id":1,"name":"홍길동","createdAt":"2024-01-15T14:30:00"}
     *
     * 왜 수동으로 JSON을 만드나?
     * - 1~2단계는 학습 목적으로 라이브러리 없이 구현
     * - 3단계(Spring)에서는 Jackson이 자동 변환
     *
     * 주의사항:
     * - name에 특수문자(", \)가 있으면 이스케이프 처리 필요
     * - 실무에서는 절대 이렇게 안 함 (보안, 유지보수 문제)
     */
    public String toJson() {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"createdAt\":\"%s\"}",
                id, name, createdAt
        );
    }
}