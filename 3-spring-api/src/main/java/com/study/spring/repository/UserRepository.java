package com.study.spring.repository;

import com.study.spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 학습 포인트:
 * 2단계: UserRepository 인터페이스 직접 설계
 *        MemoryUserRepository 구현체 직접 구현 (50줄)
 *
 * 3단계: JpaRepository 상속 한 줄로 아래가 전부 자동 제공됨
 *        - save()
 *        - findAll()
 *        - findById()
 *        - deleteById()
 *        - existsById()
 *        - count()
 *        ... 등 수십 개 메서드
 *
 * Spring Data JPA가 구현체를 자동으로 만들어줌
 */
public interface UserRepository extends JpaRepository<User, Long> {
}