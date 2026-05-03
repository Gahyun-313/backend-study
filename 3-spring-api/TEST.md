# 3-spring-api 테스트

> 실행: IntelliJ에서 SpringApiApplication.java 실행
> URL: http://localhost:8080

---

## Swagger UI 테스트

```
http://localhost:8080/swagger-ui/index.html
```
→ API 목록 확인 후 직접 테스트 가능

---

## 통합 테스트 실행

```bash
./gradlew :3-spring-api:test
```

---

## curl 시나리오 (순서대로 실행)

```bash
# 1. 유저 생성
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"홍길동"}'

# 2. 유저 추가 생성
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"김영희"}'

# 3. 전체 조회
curl http://localhost:8080/users

# 4. 단일 조회
curl http://localhost:8080/users/1

# 5. 수정
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"홍길동(수정)"}'

# 6. 삭제
curl -X DELETE http://localhost:8080/users/1

# 7. 전체 조회 (1명만 남음)
curl http://localhost:8080/users
```

---

## 에러 케이스

```bash
# name 누락 → 400
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{}'

# 없는 유저 조회 → 404
curl http://localhost:8080/users/999
```

---

## H2 콘솔

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
username: sa
password: (비워두기)
```

