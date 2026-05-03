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
        UserRequest request = new UserRequest("홍길동");

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

        UserRequest request = new UserRequest("김철수");

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
        UserRequest request = new UserRequest(name);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}