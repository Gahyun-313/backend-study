package com.study.spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 학습 포인트:
 * 2단계: isValid() 직접 구현
 * 3단계: @NotBlank 어노테이션 하나로 검증 자동화
 *        → @Valid와 함께 사용하면 Controller에서 검증 코드 불필요
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "name 필드가 필요합니다")
    private String name;
}