package com.study.mvc.dto;

/**
 * 클라이언트 → 서버로 들어오는 요청 데이터를 담는 DTO
 *
 * 학습 포인트:
 * - 1단계: Controller가 body 문자열을 직접 파싱
 * - 2단계: 요청 데이터를 구조체(DTO)로 먼저 변환 후 사용
 *
 * 왜 DTO를 사용하나?
 * - 타입 안정성: String body보다 UserRequest 객체가 안전
 * - 검증 로직 응집: isValid() 같은 검증 메서드를 DTO에 모을 수 있음
 * - 코드 가독성: request.getName()이 body 파싱보다 명확
 *
 * 실무에서는?
 * - Jackson 라이브러리가 JSON → DTO 변환을 자동으로 처리
 * - @Valid 어노테이션으로 검증 자동화
 */
public class UserRequest {
    private final String name;  // 사용자 이름 (클라이언트가 보낸 데이터)

    /**
     * UserRequest 생성자
     *
     * @param name 클라이언트가 보낸 사용자 이름
     *
     * final 필드이므로 생성 후 변경 불가 (불변 객체)
     */
    public UserRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    /**
     * 요청 데이터 유효성 검증
     *
     * 검증 규칙:
     * - name이 null이 아니어야 함
     * - name이 공백만 있는 문자열이 아니어야 함
     *
     * 사용 예시:
     * - Service 계층에서 if (!request.isValid()) throw new InvalidRequestException()
     *
     * 왜 여기서 검증하나?
     * - 요청 데이터에 대한 검증 로직은 DTO가 관리하는 것이 응집도가 높음
     * - Controller나 Service에서 검증 코드가 흩어지는 것을 방지
     */
    public boolean isValid() {
        return name != null && !name.isBlank();
    }
}