package com.study.http.controller;
import com.study.http.handler.HttpResponse;
import com.study.http.storage.UserStorage;

import com.study.http.model.User;
import com.study.http.util.JsonUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 1단계에서는 Controller가 Storage에 직접 접근
 * -> 2단계에서 Controller -> Service -> Repository로 분리
 */

public class UserController {
    private final UserStorage storage = UserStorage.getInstance();

    // Get /users
    public String getUsers() {
        List<User> users = storage.findAll();
        String json = "[" + users.stream()
                .map(User::toJson)
                .collect(Collectors.joining(",")) + "]";
        return HttpResponse.ok(JsonUtil.successResponse(json));
    }

    // POST /users
    public String createUser(String body) {
        String name = JsonUtil.extractStringField(body, "name");
        if (name == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("name 필드가 필요합니다.")
            );
        }

        User user = storage.save(name);
        return HttpResponse.created(JsonUtil.successResponse(user.toJson()));
    }

    // GET /users/{id}
    public String getUserById(String idStr) {
        Long id = parseId(idStr);
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다.")
            );
        }

        Optional<User> user = storage.findById(id);
        return user
                .map(u -> HttpResponse.ok(JsonUtil.successResponse(u.toJson())))
                .orElse(HttpResponse.notFound(JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다.")));

    }

    // PUT /users/{id}
    public String updateUser(String idStr, String body) {
        Long id = parseId(idStr);
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다.")
            );
        }

        String name = JsonUtil.extractStringField(body, "name");
        if (name == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("name 필드가 필요합니다.")
            );
        }

        Optional<User> user = storage.updateById(id, name);
        return user
                .map(u -> HttpResponse.ok(JsonUtil.successResponse(u.toJson())))
                .orElse(HttpResponse.notFound(
                        JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다")));
    }

    // DELETE /users/{id}
    public String deleteUser(String idStr) {
        Long id = parseId(idStr);
        if (id == null) {
            return HttpResponse.badRequest(
                    JsonUtil.errorResponse("유효하지 않은 id 형식입니다"));
        }

        boolean deleted = storage.deleteById(id);
        return deleted
                ? HttpResponse.ok(JsonUtil.successResponse("{\"message\":\"삭제 완료\"}"))
                : HttpResponse.notFound(
                JsonUtil.errorResponse("id " + id + "에 해당하는 유저가 없습니다"));
    }

    private Long parseId(String idStr) {
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
