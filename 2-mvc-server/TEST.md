# 2-mvc-server 테스트

> 실행: IntelliJ에서 Main.java 실행
> URL: http://localhost:8080
> 터미널: Git Bash 사용

---

## JUnit 테스트 실행

```bash
# 루트에서 실행
./gradlew :2-mvc-server:test
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

# 없는 경로 → 404
curl http://localhost:8080/unknown
```
