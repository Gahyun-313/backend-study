package com.study.mvc.repository;

import com.study.mvc.domain.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 메모리 기반 User 저장소 구현체
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
 */

public class MemoryUserRepository implements UserRepository {

    // 실제 데이터를 저장하는 Map
    private final Map<Long, User> store = new ConcurrentHashMap<>();
    // 자동 증가 ID 생성기
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 새로운 유저를 저장, 반환
    @Override
    public User save(String name) {
        Long id = idGenerator.getAndIncrement();
        User user = new User(id, name);
        store.put(id, user);
        return user;
    }

    // 저장된 모든 유저를 리스트로 반환
    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    // id로 특정 유저 조회
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    // 특정 유저의 이름을 수정
    @Override
    public Optional<User> updateById(Long id, String name) {
        User user = store.get(id);
        if (user == null) return Optional.empty();
        user.setName(name);
        return Optional.of(user);
    }

    // 특정 유저 삭제
    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    // 테스트용 초기화 메서드
    public void clear() {
        store.clear();
        idGenerator.set(1);
    }
}