# web-framework-from-scratch

HTTP 서버부터 MVC, Spring Boot까지 웹 애플리케이션 구조를 단계적으로 구현함으로써  
프레임워크 내부 동작 원리를 체계적으로 이해한 프로젝트입니다.

---
## 🎯 프로젝트 개요

Spring을 단순히 사용하는 것을 넘어,  
웹 애플리케이션이 요청을 처리하는 전체 흐름과 구조를 직접 구현하며 이해하는 것을 목표로 했습니다.

- 1️⃣ HTTP 요청/응답 처리 과정 직접 구현
- 2️⃣ MVC 아키텍처 설계 및 책임 분리 경험
- 3️⃣ Spring 구조와 비교를 통한 프레임워크 내부 동작 이해
---

## 구성

| 단계 | 프로젝트 | 설명 | 💡 |
|------|----------|-----------|-----------|
| 1 | [1-http-server](./1-http-server) | - `ServerSocket` 기반 HTTP 서버 구현 <br> - Request Line / Header / Body 직접 파싱 <br> - Response 생성 및 반환 처리 | **핵심 포인트** <br> - HTTP는 문자열 기반 프로토콜 <br> - 서버는 요청을 받아 처리하는 반복 구조 (accept loop) |
| 2 | [2-mvc-server](./2-mvc-server) | - Router / Controller / Model 구조 직접 설계 <br> - 요청 → 라우팅 → 컨트롤러 처리 흐름 구현 <br> - 비즈니스 로직과 요청 처리 로직 분리 | **문제** <br> - 요청 처리 로직과 비즈니스 로직이 혼재되어 유지보수 어려움 발생 <br> **해결** <br> - Controller / Service 역할 분리로 책임 분리 <br> **배운 점** <br> - MVC 패턴의 필요성과 계층 분리의 중요성 |
| 3 | [3-spring-api](./3-spring-api) | - Spring Boot 기반 REST API 구현 <br> - 계층형 아키텍처 적용 (Controller / Service / Repository) <br> - 공통 응답 포맷 적용 | **비교 및 이해** <br> - DispatcherServlet이 직접 구현한 Router 역할 수행 <br> - DI(의존성 주입)을 통해 객체 생성 및 관리 자동화 <br> **배운 점** <br> - 프레임워크가 해결해주는 문제 (라우팅, DI, 생명주기 관리 등) |

## 🔁 요청 처리 흐름 비교

### 직접 구현 구조

```
Client → HttpServer → RequestHandler → Router → Controller → Service → Storage
```
### Spring 구조

```
Client → DispatcherServlet → Controller → Service → Repository
```

👉 Spring은 내부적으로 위 과정을 추상화하여 제공
<br>

---
<br>

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

## 📦 공통 응답 형식

- 성공: `{"success": true, "data": {...}}`
- 실패: `{"success": false, "error": "..."}`

<br> 

--- 

## 💡 핵심 학습 포인트

- HTTP 프로토콜의 동작 원리 이해
- MVC 아키텍처의 설계 및 책임 분리
- Spring 프레임워크 내부 구조 이해
- 직접 구현을 통한 프레임워크 역할 체감

---

## 🚀 프로젝트 의의

단순히 프레임워크를 사용하는 수준을 넘어  
웹 애플리케이션이 동작하는 전체 구조를 직접 구현하고 비교함으로써  

👉 **“왜 Spring을 사용하는지 설명할 수 있는 기반”을 만든 프로젝트입니다.**

--- 
