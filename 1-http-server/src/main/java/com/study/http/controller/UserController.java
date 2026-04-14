package com.study.http.controller;
import com.study.http.handler.HttpResponse;
import com.study.http.storage.UserStorage;

import com.study.http.model.User;
import com.study.http.util.JsonUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User CRUD 작업을 처리하는 컨트롤러
 *
 * 1단계에서는 Controller가 Storage에 직접 접근
 * -> 2단계에서 Controller -> Service -> Repository로 분리 예정
 *
 * 역할:
 *  - HTTP 요청을 받아서 비즈니스 로직 수행
 *  - 입력값 검증 (id 형식, 필수 필드 체크)
 *  - Storage와 통신해서 데이터 조회/저장/수정/삭제
 *  - 결과를 HTTP 응답으로 변환 (JSON 포맷)
 *
 *  메서드:
 *  - getUsers()
 *  - createUser(String body)
 *  - getUserById(String idStr)
 *  - updateUser(String idStr, String body)
 *  - deleteUser(String idStr)
 *  - parseId(String idStr)
 */

public class UserController {
    // 싱글톤 인스턴스 - 서버 전체에서 하나의 Storage 공유
    private final UserStorage storage = UserStorage.getInstance();

    /**
     * GET /users 전체 유저 목록 조회
     *
     * {
     *     "success": true,
     *     "data": [{"id":1,"name":"홍길동"},{"id":2,"name":"김철수"}]
     * }
     */
    public String getUsers() {
        List<User> users = storage.findAll();

        // 각 User 객체를 JSON 문자열로 변환 후 배열로 묶기
        // Stream API 활용: map으로 변환 -> joining으로 콤마 연결
        String json = "[" + users.stream()
                .map(User::toJson)                  // 각 User를 JSON으로 변환
                .collect(Collectors.joining(","))   // 콤마로 연결
                + "]";
        return HttpResponse.ok(JsonUtil.successResponse(json));
    }

    /**
     * POST /users - 새로운 유저 생성
     *
     * @param body 요청 본문 (JSON 형태: {"name":"홍길동"})
     * @return 생성된 유저 정보 (201 Created) 또는 에러 (400 Bad Request)
     *
     * 검증 로직:
     * - name 필드 존재 여부 확인
     * - name이 없으면 400 에러 반환
     */
    public String createUser(String body) {
        // JSON에서 name 필드 추출
        String name = JsonUtil.extractStringField(body, "name");

        // 필수 필드 검증
        if (name == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("name 필드가 필요합니다.")
            );
        }

        // Storage에 저장 - 자동으로 id 부여됨
        User user = storage.save(name);

        // 201 Created 상태코드와 함께 생성된 유저 반환
        return HttpResponse.created(JsonUtil.successResponse(user.toJson()));
    }

    /**
     * GET /users/{id} - 특정 id의 유저 조회
     *
     * @param idStr 경로에서 추출한 id 문자열 (예: "1", "123")
     * @return 유저 정보 (200 OK) 또는 에러 (400/404)
     *
     * 검증 및 처리:
     * 1. id 문자열을 Long 타입으로 변환 (실패 시 400)
     * 2. Storage에서 해당 id로 조회 (없으면 404)
     */
    public String getUserById(String idStr) {
        // id 파싱 - 숫자가 아니면 null 반환
        Long id = parseId(idStr);
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다.")
            );
        }

        // Optional로 감싸진 결과 - 유저가 있을 수도, 없을 수도 있음
        Optional<User> user = storage.findById(id);

        return user
                .map(u -> HttpResponse.ok(JsonUtil.successResponse(u.toJson()))) // 있으면 200 OK
                .orElse(HttpResponse.notFound(JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다."))); // 없으면 404 Not Found

    }

    /**
     * PUT /users/{id} - 특정 유저 정보 수정
     *
     * @param idStr 수정할 유저의 id
     * @param body 수정할 내용 (JSON 형태: {"name":"새이름"})
     * @return 수정된 유저 정보 (200 OK) 또는 에러 (400/404)
     *
     * 검증 로직:
     * 1. id 형식 검증 (숫자인지)
     * 2. name 필드 존재 여부 검증
     * 3. 해당 id의 유저가 존재하는지 확인
     */
    public String updateUser(String idStr, String body) {
        Long id = parseId(idStr);
        // id 파싱 및 검증
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다.")
            );
        }

        // 수정할 name 추출 및 검증
        String name = JsonUtil.extractStringField(body, "name");
        if (name == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("name 필드가 필요합니다.")
            );
        }

        // Storage에서 업데이트 수행
        // 성공 시 수정된 User 반환, 실패 시 Optional.empty()
        Optional<User> user = storage.updateById(id, name);
        return user
                .map(u -> HttpResponse.ok(JsonUtil.successResponse(u.toJson())))
                .orElse(HttpResponse.notFound(
                        JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다")));
    }

    /**
     * DELETE /users/{id} - 특정 유저 삭제
     *
     * @param idStr 삭제할 유저의 id
     * @return 성공 메시지 (200 OK) 또는 에러 (400/404)
     *
     * 반환값:
     * - 삭제 성공: true (200 OK + 성공 메시지)
     * - 유저 없음: false (404 Not Found + 에러 메시지)
     */
    public String deleteUser(String idStr) {
        // id 파싱 및 검증
        Long id = parseId(idStr);
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다"));
        }

        // 삭제 수행 - 성공 시 true, 유저가 없으면 false 반환
        boolean deleted = storage.deleteById(id);

        return deleted
                ? HttpResponse.ok(JsonUtil.successResponse("{\"message\":\"삭제 완료\"}"))
                : HttpResponse.notFound(
                JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다"));
    }

    /**
     * 문자열을 Long 타입으로 변환하는 헬퍼 메서드
     *
     * @param idStr 변환할 문자열 (예: "123", "abc")
     * @return 변환 성공 시 Long 객체, 실패 시 null
     *
     * 사용 이유:
     * - URL 경로에서 추출한 id는 항상 문자열 형태
     * - 숫자가 아닌 값(예: "/users/abc")이 들어올 수 있어서 예외 처리 필요
     * - try-catch로 NumberFormatException을 잡아서 null 반환
     */
    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            // 숫자로 변환할 수 없으면 null 반환
            return null;
        }
    }

}
