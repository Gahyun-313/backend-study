package com.study.mvc.repository;

import com.study.mvc.domain.User;

import java.util.List;
import java.util.Optional;

/**
 * User 데이터 접근을 추상화한 Repository 인터페이스
 *
 * 학습 포인트:
 * 인터페이스로 추상화하면 구현체를 자유롭게 교체할 수 있음
 *
 * 2단계: MemoryUserRepository (메모리: ConcurrentHashMap)
 * 3단계: JpaRepository (DB)
 *
 * Service는 이 인터페이스에만 의존하기 때문에
 * 구현체가 바뀌어도 Service 코드는 변경 없음 (DIP: 의존관계 역전 원칙)
 *
 * DIP란?
 * - Dependency Inversion Principle (SOLID 중 D)
 * - "구체적인 것이 아니라 추상적인 것에 의존해야 함"
 * - Service가 MemoryUserRepository에 직접 의존 X
 * - Service가 UserRepository 인터페이스에 의존 O
 * - 구현체 교체 시 Service는 수정 불필요
 *
 * ---------------------------------------------------------
 * Repository의 역할:
 * - 데이터 저장소(메모리/DB)와의 통신만 담당
 * - 비즈니스 로직은 포함하지 않음 (→ Service의 역할)
 * - CRUD 기본 연산만 제공
 * ---------------------------------------------------------
 */
public interface UserRepository {

    /**
     * 새 유저 저장
     *
     * @param name 유저 이름
     * @return 저장된 User 객체 (id 자동 생성됨)
     */
    User save(String name);

    /**
     * 모든 유저 조회
     *
     * @return 전체 User 리스트
     */
    List<User> findAll();

    /**
     * id로 특정 유저 조회
     *
     * @param id 조회할 유저의 id
     * @return Optional<User> - 존재하면 User 포함, 없으면 빈 Optional
     *
     * Optional을 사용하는 이유:
     * - null 반환의 문제점: null 체크를 깜빡하면 NullPointerException
     * - Optional은 "값이 없을 수 있음"을 타입으로 명시
     * - .orElseThrow()로 예외 처리 강제 가능
     *
     * 사용 예시:
     * Optional<User> optUser = repository.findById(1L);
     * User user = optUser.orElseThrow(() -> new UserNotFoundException(1L));
     */
    Optional<User> findById(Long id);

    /**
     * id로 특정 유저의 정보 수정
     *
     * @param id 수정할 유저의 id
     * @param name 새로운 이름
     * @return Optional<User> - 수정된 User (없으면 빈 Optional)
     */
    Optional<User> updateById(Long id, String name);

    /**
     * id로 특정 유저 삭제
     *
     * @param id 삭제할 유저의 id
     * @return 삭제 성공 여부 (true: 삭제됨, false: 해당 id 없음)
     *
     * 왜 boolean 반환인가?
     * - Optional<User>를 반환할 수도 있지만
     * - 삭제는 "성공/실패" 판단만 필요하므로 boolean이 더 명확
     */
    boolean deleteById(Long id);
}