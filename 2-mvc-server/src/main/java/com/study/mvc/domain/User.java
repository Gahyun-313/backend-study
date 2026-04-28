package com.study.mvc.domain;

import java.time.LocalDateTime;

/**
 * 1단계와 동일한 도메인 객체
 * 2단계에서는 toJson()을 User가 직접 갖지 않고 UserResponse DTO가 변환을 담당
 * -> 도메인 객체가 직렬화 방식을 알 필요x
 */
public class User {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId()                 { return id; }
    public String getName()             { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setName(String name)    { this.name = name; }
}
