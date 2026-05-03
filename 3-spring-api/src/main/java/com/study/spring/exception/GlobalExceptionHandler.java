package com.study.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 학습 포인트:
 *
 * 2단계: 각 Controller 메서드마다 try-catch 작성
 * catch (UserNotFoundException e) → HttpResponse.notFound(...)
 * catch (InvalidRequestException e) → HttpResponse.badRequest(...)
 *
 * 3단계: @RestControllerAdvice 하나로 전역 예외 처리
 * → Controller 메서드에 try-catch 코드가 사라짐
 * → 예외 처리 로직이 한 곳에 모임
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 — 유저 없음 */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException e
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    /** 400 — @Valid 검증 실패 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("잘못된 요청입니다");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    /** 500 — 그 외 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류: " + e.getMessage()));
    }
}