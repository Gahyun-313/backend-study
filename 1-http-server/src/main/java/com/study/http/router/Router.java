package com.study.http.router;

import com.study.http.controller.UserController;
import com.study.http.handler.HttpRequest;
import com.study.http.handler.HttpResponse;
import com.study.http.util.JsonUtil;

/**
 * 1단계: if 문으로 라우팅 구현
 * -> 2단계: Router 클래스로 구조화
 * -> 3단계: @RequestMapping 어노테이션으로 자동화
 */

public class Router {
    private static final UserController userController = new UserController();

    public static String route(HttpRequest request) {
        String method = request.getMethod();
        String path = request.getPath();
        String body = request.getBody();

        try {
            // GET /users
            if (method.equals("GET") && path.equals("/users")) {
                return userController.getUsers();
            }
            // POST /users
            if (method.equals("POST") && path.equals("/users")) {
                return userController.createUser(body);
            }
            // GET /users/{id}
            if (method.equals("GET") && path.startsWith("/users/")) {
                String id = path.substring("/users/".length());
                return userController.getUserById(id);
            }
            // PUT /users/{id}
            if (method.equals("PUT") && path.startsWith("/users/")) {
                String id = path.substring("/users/".length());
                return userController.updateUser(id, body);
            }
            // DELETE /users/{id}
            if (method.equals("DELETE") && path.startsWith("/users/")) {
                String id = path.substring("/users/".length());
                return userController.deleteUser(id);
            }

            return HttpResponse.notFound(
                    JsonUtil.errorResponse("Route not found: " + method + " " + path)
            );

        } catch (Exception e) {
            return HttpResponse.internalError(
                    JsonUtil.errorResponse("서버 내부 오류: " + e.getMessage())
            );
        }
    }

    /** /users/3 -> "3" */
    private static String extractId(String path) {
        return path.substring("/users/".length());
    }
}
