package com.study.mvc.domain;

import java.time.LocalDateTime;

/**
 * 사용자 도메인 객체
 *
 * 1단계와의 차이점:
 * - 1단계: User가 toJson() 메서드를 가지고 있어서 스스로 JSON으로 변환
 * - 2단계: toJson()을 제거하고 UserResponse DTO가 변환을 담당
 *
 * 왜 이렇게 바뀌었나?
 * - 도메인 객체는 비즈니스 핵심 로직과 데이터만 관리해야 함
 * - JSON 변환은 표현 계층의 관심사이므로 DTO가 담당하는 것이 맞음
 * - User는 "어떻게 보여질지"를 몰라야 함 (단일 책임 원칙)
 */
public class User {
    private Long id;           // 사용자 고유 ID (Repository에서 자동 생성)
    private String name;       // 사용자 이름
    private LocalDateTime createdAt;  // 생성 시각 (생성자에서 자동 설정)

    /**
     * User 생성자
     *
     * @param id Repository에서 생성한 고유 ID
     * @param name 사용자 이름
     *
     * createdAt은 생성 시점에 자동으로 현재 시각으로 설정됨
     */
    public User(Long id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    // === Getter 메서드 ===
    // 도메인 객체의 상태를 외부에서 읽을 수 있도록 제공

    public Long getId()                 { return id; }
    public String getName()             { return name; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * name 수정용 Setter
     *
     * 왜 name만 Setter가 있나?
     * - id: 한 번 생성되면 변경 불가 (불변)
     * - createdAt: 생성 시각은 변경 불가 (불변)
     * - name: 사용자 정보 수정 시 변경 가능
     *
     * 최소한의 Setter만 제공하는 것이 도메인 객체의 불변성을 지키는 방법
     */
    public void setName(String name)    { this.name = name; }
}