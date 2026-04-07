package com.study.http.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 사용자 정보를 담는 모델 클래스
 */
public class User {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    // 사용자 객체 생성
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();   // 생성 시각을 현재 시각으로 초기화
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setName(String name) { this.name = name; }

    /**
     * User 객체를 JSON 문자열로 직렬화
     * - Jackson ObjectMapper가 자동으로 처리하는 작업을 직접 구현
     * ex. {"id":1,"name":"홍길동","createdAt":"2026-04-07T12:00:00"}
     */
    public String toJson() {
        String date = createdAt.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        );
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"createdAt\":\"%s\"}",
                id, name, date
        );
    }
}
