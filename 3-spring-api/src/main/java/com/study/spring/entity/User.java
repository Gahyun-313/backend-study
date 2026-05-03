package com.study.spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 학습 포인트:
 * 2단계의 User 도메인 객체와 비교
 *
 * 2단계: 일반 Java 클래스, 메모리에만 저장
 * 3단계: @Entity 어노테이션 하나로 DB 테이블과 매핑됨
 *
 * Spring이 자동으로 해주는 것:
 * - 테이블 생성 (ddl-auto: create-drop)
 * - id 자동 증가 (@GeneratedValue)
 * - createdAt 자동 주입 (@PrePersist)
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(String name) {
        this.name = name;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateName(String name) {
        this.name = name;
    }
}