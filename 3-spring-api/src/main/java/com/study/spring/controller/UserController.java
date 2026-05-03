package com.study.spring.controller;

import com.study.spring.dto.UserRequest;
import com.study.spring.dto.UserResponse;
import com.study.spring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 학습 포인트:
 * [1] @RestController
 *     @Controller + @ResponseBody 합친 것
 *     → 반환 객체를 Jackson이 자동으로 JSON 변환
 *     → 2단계: toJson() 직접 구현, HttpResponse 문자열 직접 조립
 *
 * [2] @RequestMapping
 *     URL 매핑을 어노테이션으로 선언
 *     → 2단계: Router if문으로 직접 분기
 *
 * [3] @Valid
 *     UserRequest의 @NotBlank 검증을 자동 실행
 *     → 2단계: isValid() 직접 호출, 예외 직접 throw
 *
 * [4] ResponseEntity
 *     상태코드 + 응답 바디를 함께 반환
 *     → 2단계: HttpResponse.ok(), HttpResponse.created() 직접 조립
 *
 * [5] 예외 처리 코드가 없음
 *     → GlobalExceptionHandler가 전역으로 처리
 *     → 2단계: 각 메서드마다 try-catch 작성
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** GET /users */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /** POST /users */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    /** GET /users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /** PUT /users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /** DELETE /users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
