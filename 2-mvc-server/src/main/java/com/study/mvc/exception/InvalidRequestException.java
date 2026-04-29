package com.study.mvc.exception;

/**
 * 잘못된 요청 데이터에 대한 예외
 *
 * 발생 상황:
 * - 필수 필드(name)가 누락되거나 비어있을 때
 * - id 형식이 잘못되었을 때 (예: "abc"를 Long으로 변환 시도)
 *
 * 학습 포인트:
 * - RuntimeException을 상속받아서 unchecked exception으로 만듦
 * - Controller에서 catch해서 400 Bad Request 응답으로 변환
 *
 * 왜 RuntimeException을 상속받나?
 * - checked exception(Exception 상속)은 try-catch 강제
 * - unchecked exception은 필요한 곳에서만 catch 가능
 * - 비즈니스 예외는 보통 unchecked로 만듦 (Spring 관례)
 *
 * 사용 예시:
 * - throw new InvalidRequestException("유효하지 않은 id 형식입니다");
 * - Controller에서 catch → HttpResponse.badRequest()
 */
public class InvalidRequestException extends RuntimeException {
    /**
     * @param message 예외 상황을 설명하는 메시지
     *                클라이언트에게 그대로 전달됨
     */
    public InvalidRequestException(String message) {
        super(message);  // 부모 클래스(RuntimeException)의 생성자 호출
    }
}