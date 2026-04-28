package com.study.mvc.exception;

/**
 * 학습 포인트:
 * 예외 클래스를 만들면 "무슨 문제인지"가 코드에 드러난다.
 * 1단계의 if (user == null) return 404 대신
 * throw new UserNotFoundException(id) 로 의도가 명확해진다.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("id" + id + "에 해당하는 유저가 없습니다.");
    }
}
