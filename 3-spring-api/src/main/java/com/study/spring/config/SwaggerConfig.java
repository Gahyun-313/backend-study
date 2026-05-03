package com.study.spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 학습 포인트:
 * Swagger UI는 Controller의 어노테이션을 읽어서 API 문서를 자동으로 생성함
 *
 * 실행 후 http://localhost:8080/swagger-ui/index.html 접속
 * → API 목록 확인 + 직접 테스트 가능
 *
 * 2단계: TEST.md에 curl 명령어 직접 작성
 * 3단계: Swagger UI에서 버튼 클릭으로 테스트
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("web-framework-from-scratch")
                        .description("HTTP → MVC → Spring 구조 비교 학습 프로젝트")
                        .version("3.0.0")
                );
    }
}