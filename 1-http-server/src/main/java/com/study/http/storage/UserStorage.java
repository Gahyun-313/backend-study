package com.study.http.storage;

import com.study.http.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 사용자 데이터를 메모리에 저장하는 저장소 클래스
 * - DB(MySQL) + JPA의 역할
 * - 지금은 메모리(Map)으로 구현해 저장소 역할 자체에 집중
 *
 * - ConcurrentHashMap: 멀티스레드 환경에서 데이터 정합성 보장
 * - AtomicLong: 스레드 안전한 ID 증가
 */

public class UserStorage {

    // 싱클톤 인스턴스 - 애플리케이션 전체에서 하나의 저장소만 사용
    private static final UserStorage INSTANCE = new UserStorage();

    /** 싱글톤 인스턴스 반환 **/
    public static UserStorage getInstance() { return INSTANCE;}

    /** 외부에서 직접 생성 불가 (싱글톤 패턴) 반환 **/
    private UserStorage() {}

    // 실제 데이터 저장소 - key: 사용자ID, value: User 객체
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    // 사용자 생성 시 고유 ID를 순차적으로 발급하는 카운터 (1부터 시작)
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 새 사용자를 저장하고 반환
     * - ID는 자동 발급
     *
     * @param name 사용자 이름
     * @return 저장된 User 객체 (자동 발급된 ID 포함)
     */
    public User save(String name) {
        Long id = idGenerator.getAndIncrement(); // 현재 ID를 가져오고 1 증가
        User user = new User(id, name);
        store.put(id, user);
        return user;
    }

    /**
     * 저장된 모든 사용자 목록 반환
     *
     * @return 전체 사용자 리스트 (순서 보장x)
     */
    public List<User> findAll() {
        return new ArrayList<>(store.values()); // 원본 store가 외부에서 수정되지 않도록 복사본 반환
    }

    /**
     *
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * ID로 사용자를 찾아 이름 수정
     *
     * @param id 수정할 사용자 ID
     * @param name 변경할 이름
     * @return 수정된 User 객체
     */
    public Optional<User> updateById(Long id, String name) {
        User user = store.get(id);
        if (user == null) return Optional.empty(); // 존재하지 않는 ID면 빈 Optional 반환
        user.setName(name);
        return Optional.of(user);
    }

    /**
     * ID로 사용자를 찾아 삭제
     *
     * @param id 삭제할 사용자 ID
     * @return 삭제 성공이면 true, 존재하지 않는 ID면 false
     */
    public boolean deleteById(Long id) {
        return store.remove(id) != null; // remove()는 삭제된 값을 반환, null이면 존재하지 않았던 것임
    }

    /**
     * 저장소의 모든 데이터를 초기화
     * - ID의 카운터도 1로 리셋
     */
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}
