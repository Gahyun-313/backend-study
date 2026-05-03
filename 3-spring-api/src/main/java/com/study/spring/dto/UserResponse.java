package com.study.spring.dto;

import com.study.spring.entity.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

/**
 * 학습 포인트:
 * 2단계와 동일한 역할이지만 Lombok으로 코드량이 줄어듦
 * Entity를 그대로 반환하지 않고 DTO로 변환하는 원칙은 동일
 */
@Getter
public class UserResponse {

    private final Long id;
    private final String name;
    private final String createdAt;

    private UserResponse(Long id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getCreatedAt().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                )
        );
    }
}