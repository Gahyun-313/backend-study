package com.study.mvc.repository;

import com.study.mvc.domain.User;
import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class MemoryUserRepositoryTest {

    private MemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryUserRepository();
    }

    @Test
    @DisplayName("유저 저장 후 id가 자동 부여되어야 한다")
    void save() {
        User user = repository.save("홍길동");
        assertNotNull(user.getId());
        assertEquals("홍길동", user.getName());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    @DisplayName("전체 조회 시 저장된 유저 수와 같아야 한다")
    void findAll() {
        repository.save("홍길동");
        repository.save("김영희");
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("ID로 조회 시 해당 유저가 반환되어야 한다")
    void findById() {
        User saved = repository.save("홍길동");
        Optional<User> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("홍길동", found.get().getName());
    }

    @Test
    @DisplayName("없는 ID 조회 시 빈 Optional이 반환되어야 한다")
    void findById_notFound() {
        Optional<User> found = repository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("수정 후 조회하면 변경된 이름이어야 한다")
    void updateById() {
        User saved = repository.save("홍길동");
        Optional<User> updated = repository.updateById(saved.getId(), "김철수");
        assertTrue(updated.isPresent());
        assertEquals("김철수", updated.get().getName());
    }

    @Test
    @DisplayName("삭제 후 조회하면 빈 Optional이어야 한다")
    void deleteById() {
        User saved = repository.save("홍길동");
        boolean deleted = repository.deleteById(saved.getId());
        assertTrue(deleted);
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("없는 ID 삭제 시 false가 반환되어야 한다")
    void deleteById_notFound() {
        boolean deleted = repository.deleteById(999L);
        assertFalse(deleted);
    }
}
