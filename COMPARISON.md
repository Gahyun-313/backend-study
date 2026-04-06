# 구조 비교 (3단계 완료 후 채울 예정)

| 항목 | 1-http-server | 2-mvc-server | 3-spring-api |
|------|--------------|--------------|--------------|
| 라우팅 | if문 | Router 클래스 | @RequestMapping |
| DI | 직접 new | 수동 주입 | 자동 DI |
| DB | List<> 메모리 | List<> 메모리 | JPA |
| 테스트 | curl | JUnit | @SpringBootTest |
| 예외 처리 | 직접 | 구조화 | @ExceptionHandler |
| 코드량 | 많음 | 중간 | 적음 |
| 생산성 | 낮음 | 중간 | 높음 |

