package com.study.mvc.controller;

import com.study.mvc.dto.UserRequest;
import com.study.mvc.dto.UserResponse;
import com.study.mvc.exception.InvalidRequestException;
import com.study.mvc.exception.UserNotFoundException;
import com.study.mvc.handler.HttpResponse;
import com.study.mvc.service.UserService;
import com.study.mvc.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HTTP 요청을 받아 Service 계층으로 위임하는 컨트롤러
 *
 * 학습 포인트:
 * 1단계와 비교해서 Controller 역할이 명확해짐
 *
 * 1단계 Controller: 검증 + 저장소 직접 접근 + 응답 조립
 * 2단계 Controller: 요청 파싱 → Service 호출 → 예외 catch → 응답 조립
 *
 * 비즈니스 로직은 모두 Service에 있음
 */
public class UserController {

    private final UserService userService;

    /**
     * 생성자 주입 방식으로 의존성 주입
     * Router에서 UserService를 생성해서 넣어줌
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users - 모든 사용자 조회
     *
     * 흐름:
     * 1. Service에서 List<UserResponse> 받아옴
     * 2. Stream API로 각 UserResponse를 JSON으로 변환
     * 3. 쉼표로 연결해서 JSON 배열 형태로 조립
     * 4. HttpResponse.ok()로 200 응답 반환
     */
    public String getUsers() {
        try {
            List<UserResponse> users = userService.getUsers();
            // [{"id":1,"name":"홍길동",...},{"id":2,"name":"김철수",...}] 형태로 조립
            String arr = "[" + users.stream()
                    .map(UserResponse::toJson)  // 각 DTO를 JSON 문자열로
                    .collect(Collectors.joining(",")) + "]";
            return HttpResponse.ok(JsonUtil.successResponse(arr));
        } catch (Exception e) {
            return HttpResponse.internalError(JsonUtil.errorResponse(e.getMessage()));
        }
    }

    /**
     * POST /users - 새 사용자 생성
     *
     * 흐름:
     * 1. body에서 "name" 필드 추출
     * 2. UserRequest DTO 생성
     * 3. Service에 생성 요청 → UserResponse 받음
     * 4. 201 Created 응답 반환
     *
     * 예외 처리:
     * - InvalidRequestException: name 누락/빈 값 → 400 Bad Request
     * - 기타 Exception: 서버 오류 → 500 Internal Server Error
     */
    public String createUser(String body) {
        try {
            UserRequest request = new UserRequest(
                    JsonUtil.extractStringField(body, "name")  // body에서 name만 추출
            );
            UserResponse response = userService.createUser(request);
            return HttpResponse.created(JsonUtil.successResponse(response.toJson()));
        } catch (InvalidRequestException e) {
            return HttpResponse.badRequest(JsonUtil.errorResponse(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.internalError(JsonUtil.errorResponse(e.getMessage()));
        }
    }

    /**
     * GET /users/{id} - 특정 사용자 조회
     *
     * 흐름:
     * 1. idStr을 Long으로 변환 (parseId 메서드 사용)
     * 2. Service에 조회 요청
     * 3. UserResponse를 JSON으로 변환해서 200 응답
     *
     * 예외 처리:
     * - InvalidRequestException: id 형식 오류 → 400 Bad Request
     * - UserNotFoundException: 존재하지 않는 사용자 → 404 Not Found
     * - 기타 Exception: 서버 오류 → 500 Internal Server Error
     */
    public String getUserById(String idStr) {
        try {
            UserResponse response = userService.getUserById(parseId(idStr));
            return HttpResponse.ok(JsonUtil.successResponse(response.toJson()));
        } catch (InvalidRequestException e) {
            return HttpResponse.badRequest(JsonUtil.errorResponse(e.getMessage()));
        } catch (UserNotFoundException e) {
            return HttpResponse.notFound(JsonUtil.errorResponse(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.internalError(JsonUtil.errorResponse(e.getMessage()));
        }
    }

    /**
     * PUT /users/{id} - 사용자 정보 수정
     *
     * 흐름:
     * 1. idStr을 Long으로 변환
     * 2. body에서 "name" 필드 추출해서 UserRequest 생성
     * 3. Service에 수정 요청 → 수정된 UserResponse 받음
     * 4. 200 OK 응답 반환
     *
     * 예외 처리:
     * - InvalidRequestException: id 형식 오류 또는 name 누락 → 400 Bad Request
     * - UserNotFoundException: 존재하지 않는 사용자 → 404 Not Found
     * - 기타 Exception: 서버 오류 → 500 Internal Server Error
     */
    public String updateUser(String idStr, String body) {
        try {
            UserRequest request = new UserRequest(
                    JsonUtil.extractStringField(body, "name")
            );
            UserResponse response = userService.updateUser(parseId(idStr), request);
            return HttpResponse.ok(JsonUtil.successResponse(response.toJson()));
        } catch (InvalidRequestException e) {
            return HttpResponse.badRequest(JsonUtil.errorResponse(e.getMessage()));
        } catch (UserNotFoundException e) {
            return HttpResponse.notFound(JsonUtil.errorResponse(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.internalError(JsonUtil.errorResponse(e.getMessage()));
        }
    }

    /**
     * DELETE /users/{id} - 사용자 삭제
     *
     * 흐름:
     * 1. idStr을 Long으로 변환
     * 2. Service에 삭제 요청 (반환값 없음)
     * 3. 성공 메시지와 함께 200 OK 응답
     *
     * 예외 처리:
     * - UserNotFoundException: 존재하지 않는 사용자 → 404 Not Found
     * - 기타 Exception: 서버 오류 → 500 Internal Server Error
     *
     * 참고: DELETE는 보통 204 No Content를 쓰지만
     *       이 프로젝트는 학습 목적으로 200 + 메시지 방식 사용
     */
    public String deleteUser(String idStr) {
        try {
            userService.deleteUser(parseId(idStr));
            return HttpResponse.ok(JsonUtil.successResponse("{\"message\":\"삭제 완료\"}"));
        } catch (UserNotFoundException e) {
            return HttpResponse.notFound(JsonUtil.errorResponse(e.getMessage()));
        } catch (Exception e) {
            return HttpResponse.internalError(JsonUtil.errorResponse(e.getMessage()));
        }
    }

    /**
     * URL path의 id 문자열을 Long 타입으로 변환
     *
     * 예시:
     * - parseId("123") → 123L (성공)
     * - parseId("abc") → InvalidRequestException 발생
     *
     * 왜 private 메서드인가?
     * - id 변환은 Controller 내부에서만 사용되는 기능
     * - 외부에서 직접 호출할 필요가 없으므로 private으로 캡슐화
     *
     * 왜 별도 메서드로 분리했나?
     * - getUserById, updateUser, deleteUser 모두 id 변환이 필요
     * - 중복 코드 제거 + 예외 처리 로직 일관성 유지
     */
    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("유효하지 않은 id 형식입니다");
        }
    }
}