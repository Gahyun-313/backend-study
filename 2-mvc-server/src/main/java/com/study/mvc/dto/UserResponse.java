package com.study.mvc.dto;

import com.study.mvc.domain.User;
import java.time.format.DateTimeFormatter;

/**
 * 학습 포인트:
 * 도메인 객체(User)를 그대로 반환하지 않고 DTO로 변환.
 * 도메인 내부 구조가 바뀌어도 API 응답 형식을 유지할 수 있음.
 * 실무에서는 @JsonProperty로 필드명을 제어.
 */
public class UserResponse {
    private final Long id;
    private final String name;
    private final String createdAt;

    private UserResponse(Long id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    /** 도메인 -> DTO 변환 **/
    public static UserResponse fromUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                )
        );
    }

    public Long getId() { return id; }
    public String getName()   { return name; }
    public String getCreatedAt() { return createdAt; }

    public String toJson() {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"createdAt\":\"%s\"}",
                id, name, createdAt
        );
    }
}
