package com.study.mvc.service;

import com.study.mvc.dto.UserRequest;
import com.study.mvc.dto.UserResponse;
import com.study.mvc.exception.InvalidRequestException;
import com.study.mvc.exception.UserNotFoundException;
import com.study.mvc.repository.MemoryUserRepository;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private MemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        // 매 테스트마다 새 인스턴스 → 테스트 간 독립성 보장
        repository = new MemoryUserRepository();
        userService = new UserService(repository);
    }

    @Test
    @DisplayName("유저 생성 시 id가 자동 부여되어야 한다")
    void createUser() {
        UserResponse response = userService.createUser(new UserRequest("홍길동"));
        assertNotNull(response.getId());
        assertEquals("홍길동", response.getName());
    }

    @Test
    @DisplayName("name이 없으면 InvalidRequestException이 발생해야 한다")
    void createUser_invalidName() {
        assertThrows(InvalidRequestException.class,
                () -> userService.createUser(new UserRequest(null)));
    }

    @Test
    @DisplayName("빈 name이면 InvalidRequestException이 발생해야 한다")
    void createUser_blankName() {
        assertThrows(InvalidRequestException.class,
                () -> userService.createUser(new UserRequest("  ")));
    }

    @Test
    @DisplayName("전체 조회 시 생성한 유저 수와 같아야 한다")
    void getUsers() {
        userService.createUser(new UserRequest("홍길동"));
        userService.createUser(new UserRequest("김영희"));
        List<UserResponse> users = userService.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("없는 ID 조회 시 UserNotFoundException이 발생해야 한다")
    void getUserById_notFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(999L));
    }

    @Test
    @DisplayName("유저 수정 후 조회하면 변경된 이름이어야 한다")
    void updateUser() {
        UserResponse created = userService.createUser(new UserRequest("홍길동"));
        UserResponse updated = userService.updateUser(created.getId(), new UserRequest("김철수"));
        assertEquals("김철수", updated.getName());
    }

    @Test
    @DisplayName("없는 ID 수정 시 UserNotFoundException이 발생해야 한다")
    void updateUser_notFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(999L, new UserRequest("김철수")));
    }

    @Test
    @DisplayName("유저 삭제 후 조회하면 UserNotFoundException이 발생해야 한다")
    void deleteUser() {
        UserResponse created = userService.createUser(new UserRequest("홍길동"));
        userService.deleteUser(created.getId());
        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(created.getId()));
    }

    @Test
    @DisplayName("없는 ID 삭제 시 UserNotFoundException이 발생해야 한다")
    void deleteUser_notFound() {
        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(999L));
    }
}