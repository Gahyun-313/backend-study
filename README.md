# backend-study

백엔드 동작 원리를 단계적으로 이해하기 위한 학습 프로젝트

## 구성

| 단계 | 프로젝트 | 핵심 기술 |
|------|----------|-----------|
| 1단계 | [1-http-server](./1-http-server) | Java ServerSocket |
| 2단계 | [2-mvc-server](./2-mvc-server) | MVC 직접 구현 |
| 3단계 | [3-spring-api](./3-spring-api) | Spring Boot |

## 공통 도메인

User: id / name / createdAt

## 공통 API

| Method | Path | 설명 |
|--------|------|------|
| GET | /users | 전체 조회 |
| POST | /users | 생성 |
| GET | /users/{id} | 단일 조회 |
| PUT | /users/{id} | 수정 |
| DELETE | /users/{id} | 삭제 |

## 공통 응답 형식

성공: `{"success": true, "data": {...}}`
실패: `{"success": false, "error": "..."}`

