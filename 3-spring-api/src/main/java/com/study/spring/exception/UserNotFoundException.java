package com.study.spring.exception;

/**
 * 학습 포인트:
 * 2단계와 동일한 예외 클래스.
 * 3단계에서는 @RestControllerAdvice가 이 예외를 잡아서 자동으로 응답으로 변환해줌
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("id " + id + "에 해당하는 유저가 없습니다");
    }
}