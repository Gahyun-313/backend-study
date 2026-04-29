package com.study.mvc.router;

import com.study.mvc.controller.UserController;
import com.study.mvc.handler.HttpRequest;
import com.study.mvc.handler.HttpResponse;
import com.study.mvc.repository.MemoryUserRepository;
import com.study.mvc.repository.UserRepository;
import com.study.mvc.service.UserService;
import com.study.mvc.util.JsonUtil;

/**
 * HTTP 요청을 적절한 Controller 메서드로 라우팅하는 라우터
 *
 * 역할:
 * 1. 의존성 수동 조립 (Repository → Service → Controller)
 * 2. HTTP method + path 조합으로 적절한 Controller 메서드 호출
 * 3. 존재하지 않는 경로 처리 (404 Not Found)
 *
 * 학습 포인트:
 * Router에서 의존성을 직접 조립함 (수동 DI)
 *
 * 조립 순서: Repository → Service → Controller
 * - Repository는 아무것도 의존하지 않음
 * - Service는 Repository에 의존
 * - Controller는 Service에 의존
 *
 * 3단계 Spring에서는?
 * - 이 조립을 IoC 컨테이너가 자동으로 함
 * - @Component, @Service, @Repository, @Autowired 어노테이션 사용
 * - Router 클래스 자체가 필요 없어짐 (DispatcherServlet이 담당)
 *
 * 1단계와의 차이:
 * - 1단계: RequestHandler에서 라우팅 직접 처리
 * - 2단계: Router 클래스로 분리 (관심사 분리)
 */
public class Router {

    private final UserController userController;

    /**
     * Router 생성자 - 의존성 수동 조립
     *
     * 조립 과정:
     * 1. MemoryUserRepository 생성 (데이터 저장소)
     * 2. UserService 생성 (repository 주입)
     * 3. UserController 생성 (service 주입)
     *
     * 왜 인터페이스 타입으로 선언하나?
     * - UserRepository repository = new MemoryUserRepository();
     * - 구현체(MemoryUserRepository)가 아닌 인터페이스(UserRepository)로 선언
     * - 나중에 new JpaUserRepository()로 교체 가능
     * - Service는 인터페이스만 알면 됨 (DIP)
     *
     * 이것이 바로 수동 DI (Dependency Injection):
     * - Service가 스스로 Repository를 new로 만들지 않음
     * - 외부(Router)에서 만들어서 생성자로 주입
     * - 의존성을 외부에서 조립하는 것 = DI
     */
    public Router() {
        // 수동 DI: 의존성을 직접 조립
        UserRepository repository = new MemoryUserRepository();
        UserService service = new UserService(repository);
        this.userController = new UserController(service);
    }

    /**
     * HTTP 요청을 적절한 Controller 메서드로 라우팅
     *
     * @param req HttpRequest 객체 (method, path, body 포함)
     * @return HTTP 응답 메시지 (Controller가 생성한 응답)
     *
     * 라우팅 규칙:
     * - GET    /users       → getUsers()
     * - POST   /users       → createUser(body)
     * - GET    /users/{id}  → getUserById(id)
     * - PUT    /users/{id}  → updateUser(id, body)
     * - DELETE /users/{id}  → deleteUser(id)
     * - 그 외              → 404 Not Found
     *
     * 정규식 사용:
     * - /users/\\d+ : "/users/" 뒤에 숫자가 1개 이상
     * - \\d는 [0-9]와 동일 (숫자 한 자리)
     * - +는 1개 이상 반복
     * - 예시: /users/1, /users/123 매치 O / /users/abc 매치 X
     */
    public String route(HttpRequest req) {
        String method = req.getMethod();  // GET, POST, PUT, DELETE
        String path   = req.getPath();    // /users, /users/1
        String body   = req.getBody();    // POST, PUT에서만 존재

        try {
            // GET /users - 전체 조회
            if (method.equals("GET")    && path.equals("/users"))
                return userController.getUsers();

            // POST /users - 생성
            if (method.equals("POST")   && path.equals("/users"))
                return userController.createUser(body);

            // GET /users/{id} - 단건 조회
            if (method.equals("GET")    && path.matches("/users/\\d+"))
                return userController.getUserById(extractId(path));

            // PUT /users/{id} - 수정
            if (method.equals("PUT")    && path.matches("/users/\\d+"))
                return userController.updateUser(extractId(path), body);

            // DELETE /users/{id} - 삭제
            if (method.equals("DELETE") && path.matches("/users/\\d+"))
                return userController.deleteUser(extractId(path));

            // 매칭되는 라우트가 없으면 404
            return HttpResponse.notFound(
                    JsonUtil.errorResponse("Route not found: " + method + " " + path)
            );

        } catch (Exception e) {
            // Controller에서 처리되지 않은 예외 발생 시 500
            // 실제로는 Controller에서 대부분의 예외를 catch하므로 여기까지 오는 경우는 드뭄
            return HttpResponse.internalError(
                    JsonUtil.errorResponse("서버 내부 오류: " + e.getMessage())
            );
        }
    }

    /**
     * path에서 id 부분만 추출
     *
     * @param path "/users/123" 형태의 경로
     * @return "123" (문자열로 반환, Controller에서 Long으로 변환)
     *
     * 동작 원리:
     * - "/users/".length() = 7
     * - "/users/123".substring(7) = "123"
     *
     * 왜 static인가?
     * - 인스턴스 변수(userController)를 사용하지 않음
     * - 순수 유틸리티 메서드
     * - static으로 만들어서 메모리 효율 향상
     */
    private static String extractId(String path) {
        return path.substring("/users/".length());
    }
}