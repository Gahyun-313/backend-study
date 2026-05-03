# 구조 비교 — web-framework-from-scratch

## 한 줄 요약

| 단계 | 핵심 |
|------|------|
| 1단계 | HTTP가 문자열임을 직접 체감 |
| 2단계 | 계층 분리의 필요성을 직접 체감 |
| 3단계 | Spring이 무엇을 자동화하는지 체감 |

---

## 구조 비교표

| 항목 | 1-http-server | 2-mvc-server | 3-spring-api |
|------|--------------|--------------|--------------|
| 서버 실행 | ServerSocket 직접 구현 | ServerSocket 직접 구현 | Tomcat 자동 |
| 라우팅 | if문 | if문 (Router 클래스) | @RequestMapping |
| DI | 직접 new | 수동 조립 (Router에서) | @Autowired 자동 |
| DB | ConcurrentHashMap | ConcurrentHashMap | JPA + H2 |
| 요청 파싱 | InputStream 직접 읽기 | InputStream 직접 읽기 | Jackson 자동 |
| 응답 생성 | HTTP 문자열 직접 조립 | HTTP 문자열 직접 조립 | Jackson 자동 |
| 검증 | null 체크 직접 | isValid() 직접 | @Valid + @NotBlank |
| 예외 처리 | if문으로 직접 | 예외 클래스 설계 | @RestControllerAdvice |
| 테스트 | curl 수동 | JUnit 단위 테스트 | MockMvc 통합 테스트 |
| API 문서 | TEST.md 수동 작성 | TEST.md 수동 작성 | Swagger 자동 생성 |
| 코드량 | 많음 | 중간 | 적음 |
| 생산성 | 낮음 | 중간 | 높음 |
| 제어력 | 높음 | 높음 | 낮음 |

---

## 단계별 핵심 학습

### 1단계 — HTTP 서버 직접 구현

```
ServerSocket.accept()   → 요청 대기 (블로킹)
InputStream 읽기        → HTTP 문자열 파싱
Request Line 파싱       → method, path 추출
Content-Length 헤더     → body 크기만큼 정확히 읽기
HTTP 응답 조립          → CRLF(\r\n) 구분자 직접 작성
```

**깨달음**
- HTTP는 문자열 기반 프로토콜이다
- 서버는 요청을 기다리는 프로그램이다
- 라우팅은 결국 조건문이다
- JSON도 결국 문자열이다

---

### 2단계 — MVC 구조 직접 구현

```
1단계 Controller (비대)
 ↓ 분리
Controller  → 요청 파싱, 응답 조립
Service     → 비즈니스 로직
Repository  → 데이터 저장/조회 (인터페이스 분리)
```

**깨달음**
- 계층 분리를 하면 각 클래스의 역할이 명확해진다
- 인터페이스로 추상화하면 구현체를 자유롭게 교체할 수 있다
- 계층 분리를 해야 단위 테스트가 가능해진다
- Spring의 DI가 Router의 수동 조립을 자동화한 것이다

---

### 3단계 — Spring Boot

```
2단계 수동 구현        →  Spring 자동화
───────────────────────────────────────
ServerSocket           →  Tomcat (내장)
new UserService(repo)  →  @Autowired
if문 라우팅            →  @RequestMapping
isValid() 직접 구현    →  @Valid + @NotBlank
각 메서드 try-catch    →  @RestControllerAdvice
toJson() 직접 구현     →  Jackson 자동 변환
MemoryUserRepository   →  JpaRepository
curl 수동 테스트       →  MockMvc 자동화
TEST.md 수동 작성      →  Swagger 자동 생성
```

**깨달음**
- Spring은 반복되는 코드를 어노테이션으로 자동화한다
- IoC: 객체 생성과 관리를 개발자가 아닌 Spring이 담당한다
- DI: 필요한 객체를 직접 생성하지 않고 주입받는다
- 생산성이 높아지는 대신 내부 동작을 모르면 디버깅이 어렵다

---

## Spring이 해결하는 문제

| 문제 | Spring 해결책 |
|------|--------------|
| 매번 객체를 직접 생성해야 함 | IoC 컨테이너 + @Autowired |
| 라우팅 코드가 길고 반복됨 | @RequestMapping |
| 검증 코드가 반복됨 | @Valid + @NotBlank |
| 예외 처리 코드가 반복됨 | @RestControllerAdvice |
| JSON 변환을 직접 해야 함 | Jackson 자동 변환 |
| DB 연동 코드가 복잡함 | Spring Data JPA |
| 테스트가 어려움 | @SpringBootTest + MockMvc |

---

## 구조 선택 기준

| 상황 | 선택 |
|------|------|
| HTTP/서버 동작 원리 학습 | 1단계 방식 |
| 프레임워크 없이 빠른 프로토타입 | 2단계 방식 |
| 실무 서비스 개발 | 3단계 방식 (Spring Boot) |

---

## 💡

백엔드의 동작 원리를 이해하기 위해

1. **HTTP 서버를 직접 구현**하여
   소켓, HTTP 파싱, 라우팅의 본질을 이해했고

2. **MVC 구조를 직접 설계**하여
   계층 분리와 테스트 가능한 구조를 이해했으며

3. **동일 기능을 Spring으로 재구현**하여
   프레임워크가 자동화하는 영역과 그 이유를 비교했습니다.