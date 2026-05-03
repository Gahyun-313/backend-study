package com.study.spring.service;

import com.study.spring.dto.UserRequest;
import com.study.spring.dto.UserResponse;
import com.study.spring.entity.User;
import com.study.spring.exception.UserNotFoundException;
import com.study.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 학습 포인트:
 * [1] @Service
 *     Spring이 이 클래스를 Bean으로 등록
 *     → 2단계: Router에서 new UserService(repository) 직접 생성
 *     → 3단계: Spring IoC 컨테이너가 자동으로 생성/관리
 *
 * [2] @RequiredArgsConstructor
 *     final 필드의 생성자를 Lombok이 자동 생성
 *     → 2단계: 생성자 직접 작성
 *     → 3단계: 어노테이션 하나로 생성자 주입 완성
 *
 * [3] @Transactional
 *     DB 작업을 트랜잭션으로 묶어줌
 *     → 중간에 오류 발생 시 자동 롤백
 *     → 2단계: 트랜잭션 개념 자체가 없었음
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = new User(request.getName());
        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.updateName(request.getName());
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}