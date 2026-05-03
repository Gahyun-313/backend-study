package com.study.spring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.spring.dto.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 학습 포인트:
 * [1] @SpringBootTest
 *     실제 Spring 컨텍스트를 띄워서 테스트
 *     → 2단계: new UserService(repository) 직접 생성
 *     → 3단계: Spring이 모든 Bean을 자동으로 주입
 *
 * [2] MockMvc
 *     실제 HTTP 요청/응답을 시뮬레이션
 *     → 2단계: curl로 수동 테스트
 *     → 3단계: 코드로 자동화된 HTTP 테스트
 *
 * [3] @DirtiesContext
 *     각 테스트마다 DB를 초기화
 *     → 테스트 간 데이터 독립성 보장
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 생성 시 201 상태코드와 생성된 유저가 반환되어야 한다")
    void createUser() throws Exception {
        UserRequest request = new UserRequest();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("전체 유저 조회 시 200 상태코드와 유저 목록이 반환되어야 한다")
    void getUsers() throws Exception {
        // 유저 2명 생성
        createTestUser("홍길동");
        createTestUser("김영희");

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("단일 유저 조회 시 200 상태코드와 해당 유저가 반환되어야 한다")
    void getUserById() throws Exception {
        createTestUser("홍길동");

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("없는 유저 조회 시 404 상태코드가 반환되어야 한다")
    void getUserById_notFound() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("유저 수정 시 200 상태코드와 수정된 유저가 반환되어야 한다")
    void updateUser() throws Exception {
        createTestUser("홍길동");

        UserRequest request = new UserRequest();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("김철수"));
    }

    @Test
    @DisplayName("유저 삭제 시 204 상태코드가 반환되어야 한다")
    void deleteUser() throws Exception {
        createTestUser("홍길동");

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("없는 유저 삭제 시 404 상태코드가 반환되어야 한다")
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("name 누락 시 400 상태코드가 반환되어야 한다")
    void createUser_invalidRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /** 테스트용 유저 생성 헬퍼 */
    private void createTestUser(String name) throws Exception {
        UserRequest request = new UserRequest();
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}