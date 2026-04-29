package com.study.mvc.repository;

import com.study.mvc.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * User 데이터 접근을 추상화한 Repository 인터페이스
 *
 * 학습 포인트:
 * 인터페이스로 추상화하면 구현체를 자유롭게 교체할 수 있다.
 *
 * 2단계 -> MemoryUserRepository (메모리: ConcurrentHashMap)
 * 3단계 -> JpaRepository (DB)
 *
 * Service는 이 인터페이스에만 의존하기 때문에
 * 구현체가 바뀌어도 Service 코드는 변경 없음 (DIP: 의존관계 역전 원칙)
 *
 * ---------------------------------------------------------
 *  * Repository의 역할:
 *  * - 데이터 저장소(메모리/DB)와의 통신만 담당
 *  * - 비즈니스 로직은 포함하지 않음 (-> Service의 역할)
 *  * - CRUD 기본 연산만 제공
 *  * ---------------------------------------------------------
 */

public interface UserRepository {
    User save(String name);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> updateById(Long id, String name);
    // Optional: null 반환보다 명시적으로 값이 없을 수도 있음을 표현
    boolean deleteById(Long id);
}
