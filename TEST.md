# 1-http-server API 테스트

> 실행: Main.java 실행
> URL: http://localhost:8080
> 터미널: Bash 사용

---

## 전체 시나리오 (순서대로 실행)

```bash
# 1. 유저 생성
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"홍길동"}'
# 기대: 201 / {"success":true,"data":{"id":1,"name":"홍길동","createdAt":"..."}}

# 2. 유저 추가 생성
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"김영희"}'
# 기대: 201 / {"success":true,"data":{"id":2,"name":"김영희","createdAt":"..."}}

# 3. 전체 조회 (2명 확인)
curl http://localhost:8080/users
# 기대: 200 / {"success":true,"data":[{"id":1,...},{"id":2,...}]}

# 4. 단일 조회
curl http://localhost:8080/users/1
# 기대: 200 / {"success":true,"data":{"id":1,"name":"홍길동",...}}

# 5. 수정
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"홍길동(수정)"}'
# 기대: 200 / {"success":true,"data":{"id":1,"name":"홍길동(수정)",...}}

# 6. 수정 확인
curl http://localhost:8080/users/1
# 기대: 200 / {"success":true,"data":{"id":1,"name":"홍길동(수정)",...}}

# 7. 삭제
curl -X DELETE http://localhost:8080/users/1
# 기대: 200 / {"success":true,"data":{"message":"삭제 완료"}}

# 8. 전체 조회 (1명만 남음)
curl http://localhost:8080/users
# 기대: 200 / {"success":true,"data":[{"id":2,"name":"김영희",...}]}
```

---

## 에러 케이스

```bash
# name 누락 → 400
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{}'
# 기대: 400 / {"success":false,"error":"name 필드가 필요합니다"}

# 없는 유저 조회 → 404
curl http://localhost:8080/users/999
# 기대: 404 / {"success":false,"error":"id 999에 해당하는 유저가 없습니다"}

# 없는 유저 수정 → 404
curl -X PUT http://localhost:8080/users/999 \
  -H "Content-Type: application/json" \
  -d '{"name":"테스트"}'
# 기대: 404 / {"success":false,"error":"id 999에 해당하는 유저가 없습니다"}

# 없는 유저 삭제 → 404
curl -X DELETE http://localhost:8080/users/999
# 기대: 404 / {"success":false,"error":"id 999에 해당하는 유저가 없습니다"}

# 없는 경로 → 404
curl http://localhost:8080/unknown
# 기대: 404 / {"success":false,"error":"Route not found: GET /unknown"}
```