package com.study.mvc.repository;

import com.study.mvc.domain.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 메모리 기반 User 저장소 구현체
 *
 * 역할:
 * - User 도메인 객체를 메모리(Map)에 저장/조회/수정/삭제
 * - UserRepository 인터페이스의 구현체
 *
 * 특징:
 * - ConcurrentHashMap으로 멀티스레드 환경에서 안전하게 데이터 관리
 * - AtomicLong으로 id 자동 증가 (동시 요청에도 중복 없이 생성)
 * - 서버 재시작 시 데이터 소실 (메모리에만 저장)
 *
 * 1단계(UserStorage)와의 차이점:
 * - 인터페이스 구현으로 교체 가능성 확보
 * - Repository라는 명확한 계층 분리
 * - Service에서 의존성 주입받아 사용 (new로 직접 생성 X)
 *
 * 3단계에서는?
 * - JpaRepository로 교체됨 (DB 저장)
 * - Service 코드는 변경 없이 구현체만 교체 가능
 */
public class MemoryUserRepository implements UserRepository {

    /**
     * 실제 데이터를 저장하는 Map
     *
     * 왜 ConcurrentHashMap인가?
     * - 일반 HashMap은 멀티스레드 환경에서 동시 수정 시 오류 발생
     * - ConcurrentHashMap은 내부적으로 락을 사용해 동시성 보장
     * - 여러 스레드가 동시에 put/get 해도 안전
     *
     * Key: User의 id (Long)
     * Value: User 객체
     */
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    /**
     * 자동 증가 ID 생성기
     *
     * 왜 AtomicLong인가?
     * - 일반 long은 동시에 증가시키면 중복 ID 발생 가능
     * - AtomicLong.getAndIncrement()는 원자적 연산 (thread-safe)
     * - 스레드 A가 1 받을 때, 스레드 B는 2를 받음 (절대 중복 안 됨)
     *
     * 초기값 1: 첫 번째 유저는 id=1
     */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 새로운 유저를 저장하고 반환
     *
     * @param name 저장할 유저의 이름
     * @return 저장된 User 객체 (id 자동 생성됨)
     *
     * 처리 과정:
     * 1. idGenerator에서 새 id 생성 (1 → 2 → 3...)
     * 2. User 객체 생성 (생성 시각 자동 설정됨)
     * 3. store에 저장
     * 4. 생성된 User 반환 (Service에서 DTO로 변환할 것임)
     */
    @Override
    public User save(String name) {
        Long id = idGenerator.getAndIncrement();  // 1씩 증가하며 id 발급
        User user = new User(id, name);
        store.put(id, user);
        return user;
    }

    /**
     * 저장된 모든 유저를 리스트로 반환
     *
     * @return 모든 User 객체 리스트
     *
     * 왜 new ArrayList<>로 감싸나?
     * - store.values()는 컬렉션 뷰 (원본과 연결됨)
     * - 원본을 보호하기 위해 새 리스트로 복사해서 반환
     * - Service가 리스트를 수정해도 store에 영향 없음
     *
     * 순서 보장 안 됨:
     * - ConcurrentHashMap은 삽입 순서 보장 X
     * - 순서가 필요하면 Service에서 정렬
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * id로 특정 유저 조회
     *
     * @param id 조회할 유저의 id
     * @return Optional<User> - 존재하면 User 포함, 없으면 빈 Optional
     *
     * 왜 Optional을 반환하나?
     * - null 반환보다 명시적: "값이 없을 수도 있다"를 타입으로 표현
     * - Service에서 .orElseThrow()로 예외 처리 가능
     * - NullPointerException 방지
     *
     * 사용 예시 (Service에서):
     * User user = repository.findById(1L)
     *     .orElseThrow(() -> new UserNotFoundException(1L));
     */
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));  // null이면 빈 Optional 반환
    }

    /**
     * 특정 유저의 이름을 수정
     *
     * @param id 수정할 유저의 id
     * @param name 새로운 이름
     * @return Optional<User> - 수정된 User (없으면 빈 Optional)
     *
     * 처리 과정:
     * 1. store에서 User 가져옴
     * 2. 존재하지 않으면 빈 Optional 반환
     * 3. 존재하면 setName()으로 수정
     * 4. 수정된 User를 Optional로 감싸서 반환
     *
     * 주의:
     * - User는 참조 타입이므로 setName()만 해도 store에 반영됨
     * - 별도로 put()을 다시 할 필요 없음
     */
    @Override
    public Optional<User> updateById(Long id, String name) {
        User user = store.get(id);
        if (user == null) return Optional.empty();
        user.setName(name);  // 참조 객체 수정 → store에 자동 반영
        return Optional.of(user);
    }

    /**
     * 특정 유저 삭제
     *
     * @param id 삭제할 유저의 id
     * @return true면 삭제 성공, false면 해당 id 없음
     *
     * Map.remove() 동작:
     * - 키가 존재하면 value 반환 후 삭제
     * - 키가 없으면 null 반환
     *
     * 왜 boolean으로 반환하나?
     * - Service에서 삭제 성공 여부를 판단하기 위함
     * - 실제로는 findById()로 먼저 확인하므로 거의 항상 true
     */
    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;  // 삭제된 객체가 있으면 true
    }

    /**
     * 테스트용 초기화 메서드
     *
     * 역할:
     * - 모든 데이터 삭제
     * - id 생성기를 1로 리셋
     *
     * 왜 필요한가?
     * - 단위 테스트에서 각 테스트마다 깨끗한 상태로 시작하기 위함
     * - @BeforeEach에서 repository.clear() 호출
     *
     * 실무에서는?
     * - DB 사용 시 테스트 DB를 별도로 만들거나
     * - @Transactional + @Rollback으로 테스트 후 자동 롤백
     */
    public void clear() {
        store.clear();
        idGenerator.set(1);  // 다음 ID를 1로 초기화
    }
}