package com.study.mvc.exception;

/**
 * 존재하지 않는 사용자 조회 시 발생하는 예외
 *
 * 발생 상황:
 * - GET /users/{id}에서 해당 id가 저장소에 없을 때
 * - PUT /users/{id}에서 수정하려는 사용자가 없을 때
 * - DELETE /users/{id}에서 삭제하려는 사용자가 없을 때
 *
 * 학습 포인트:
 * 예외 클래스를 만들면 "무슨 문제인지"가 코드에 드러남
 *
 * 1단계:
 *   User user = storage.get(id);
 *   if (user == null) return HttpResponse.notFound(...);
 *   → null 체크가 곳곳에 흩어져 있고, 의도가 불명확
 *
 * 2단계:
 *   User user = repository.findById(id)
 *       .orElseThrow(() -> new UserNotFoundException(id));
 *   → 예외 이름만 봐도 "유저를 못 찾았구나" 바로 이해 가능
 *   → Controller에서 catch해서 404 Not Found 응답으로 변환
 *
 * 왜 이렇게 하면 좋은가?
 * - 예외 타입으로 문제를 구분 가능 (InvalidRequest vs UserNotFound)
 * - Controller에서 각 예외마다 다른 HTTP 상태 코드 반환 가능
 * - 예외 메시지에 문맥(id 값) 포함 가능
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * @param id 찾으려고 했던 사용자의 ID
     *
     * 생성자에서 자동으로 메시지 조합:
     * - "id1에 해당하는 유저가 없습니다."
     * - "id999에 해당하는 유저가 없습니다."
     *
     * 사용 예시:
     * - throw new UserNotFoundException(1L);
     * - Controller에서 catch → HttpResponse.notFound()
     */
    public UserNotFoundException(Long id) {
        super("id" + id + "에 해당하는 유저가 없습니다.");
    }
}