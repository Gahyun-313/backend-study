package com.study.mvc.service;

import com.study.mvc.domain.User;
import com.study.mvc.dto.UserRequest;
import com.study.mvc.dto.UserResponse;
import com.study.mvc.exception.InvalidRequestException;
import com.study.mvc.exception.UserNotFoundException;
import com.study.mvc.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User 관련 비즈니스 로직을 처리하는 Service 계층
 *
 * 학습 포인트:
 *
 * [1] Service는 비즈니스 로직만 담당
 *      HTTP, 소켓, JSON 같은 기술적 관심사를 전혀 모른다.
 *      - "유효한 요청인가?", "데이터가 존재하는가?" 같은 비즈니스 규칙만 검증
 *      - HTTP 상태 코드, JSON 파싱 같은 건 Controller의 책임
 *
 * [2] Repository 인터페이스에만 의존
 *      MemoryUserRepository인지 JpaRepository인지 모른다.
 *      - "저장해줘", "조회해줘"만 요청하고 어떻게 저장하는지는 관심 없음
 *      - 메모리든 DB든 Redis든 Repository 구현체만 바꾸면 됨
 *
 * [3] 생성자 주입 (수동 DI - Dependency Injection)
 *      new UserService(repository)로 외부에서 주입받는다.
 *      -> 테스트 시 Mock Repository를 주입할 수 있다.
 *      -> 3단계에서 Spring이 이 주입을 자동으로 해준다 (@Autowired)
 *
 * ---------------------------------------------------------
 * Service의 책임:
 * - 입력값 검증 (비즈니스 규칙)
 * - Repository를 통한 데이터 처리
 * - Domain → DTO 변환
 * - 예외 발생 (비즈니스 예외)
 *
 * Service가 하지 않는 것:
 * - HTTP 요청/응답 처리 (Controller가 함)
 * - JSON 파싱/생성 (Controller가 함)
 * - 데이터 저장 방식 결정 (Repository가 함)
 * ---------------------------------------------------------
 */
public class UserService {

    /**
     * Repository 인터페이스 의존
     *
     * final 키워드:
     * - 생성자에서 한 번 할당되면 변경 불가
     * - 불변성 보장 → 스레드 안전성 향상
     *
     * 인터페이스 타입으로 선언:
     * - 구체적 구현체(MemoryUserRepository) 몰라도 됨
     * - 나중에 JpaUserRepository로 교체해도 이 코드는 변경 없음
     */
    private final UserRepository repository;

    /**
     * 생성자 주입 방식의 DI (Dependency Injection)
     *
     * 수동 DI (2단계):
     * UserRepository repo = new MemoryUserRepository();
     * UserService service = new UserService(repo);  // 외부에서 주입
     *
     * 자동 DI (3단계 Spring):
     * @Autowired  // Spring이 자동으로 주입
     * public UserService(UserRepository userRepository) { ... }
     *
     * 생성자 주입의 장점:
     * 1. 의존성이 명확히 드러남 (Service는 Repository 필요함을 명시)
     * 2. final 사용 가능 → 불변성
     * 3. 테스트 시 Mock 객체 주입 쉬움
     *
     * @param userRepository Repository 구현체 (외부에서 전달)
     */
    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    /**
     * 새로운 유저 생성
     *
     * 처리 흐름:
     * 1. 입력 DTO 유효성 검증 (비즈니스 규칙)
     * 2. Repository에 저장 요청
     * 3. Domain 객체를 응답 DTO로 변환
     *
     * DTO 사용 이유:
     * - UserRequest: Controller에서 받은 요청 데이터를 담는 객체
     * - UserResponse: Controller에 반환할 응답 데이터를 담는 객체
     * - Domain(User)과 분리하여 외부 인터페이스 변경에 유연함
     *
     * 예외 처리:
     * - 유효하지 않은 요청 → InvalidRequestException 발생
     * - Controller에서 이 예외를 잡아서 400 Bad Request로 변환
     *
     * @param request 생성 요청 DTO (name 포함)
     * @return 생성된 유저 정보를 담은 응답 DTO
     * @throws InvalidRequestException name이 null이거나 빈 문자열일 때
     */
    public UserResponse createUser(UserRequest request) {
        // 1. 비즈니스 규칙 검증: name 필드 필수
        // request.isValid()는 DTO 내부에서 name != null && !name.isBlank() 체크
        if (!request.isValid()) {
            throw new InvalidRequestException("name 필드가 필요합니다.");
        }

        // 2. Repository에 저장 (어떻게 저장하는지는 몰라도 됨)
        User user = repository.save(request.getName());

        // 3. Domain → DTO 변환
        // UserResponse.from(user)는 User 객체에서 필요한 필드만 추출
        // 예: User(1L, "홍길동") → UserResponse(1L, "홍길동")
        return UserResponse.from(user);
    }

    /**
     * 전체 유저 목록 조회
     *
     * Stream API 활용:
     * 1. repository.findAll() → List<User> (Domain 리스트)
     * 2. stream() → Stream<User>로 변환
     * 3. map(UserResponse::from) → 각 User를 UserResponse로 변환
     * 4. collect(Collectors.toList()) → List<UserResponse>로 수집
     *
     * 왜 DTO로 변환하는가?
     * - Controller는 Domain 객체를 직접 모르게 함
     * - 나중에 User에 password 필드가 추가되어도
     *   UserResponse에는 포함 안 시키면 외부 노출 방지
     *
     * 빈 리스트 처리:
     * - 유저가 없으면 빈 리스트 [] 반환 (null 아님)
     * - Controller에서 안전하게 처리 가능
     *
     * @return 전체 유저 정보를 담은 응답 DTO 리스트
     */
    public List<UserResponse> getUsers() {
        return repository.findAll().stream()
                .map(UserResponse::from)              // User → UserResponse 변환
                .collect(Collectors.toList());        // List로 수집
    }

    /**
     * 특정 id의 유저 조회
     *
     * Optional 처리 패턴:
     * - repository.findById(id) → Optional<User> 반환
     * - Optional.orElseThrow() → 값이 없으면 예외 발생
     *
     * 예외 처리 전략:
     * - 유저가 없으면 UserNotFoundException 발생
     * - Controller에서 이 예외를 잡아서 404 Not Found로 변환
     *
     * 1단계와의 차이:
     * - 1단계: Optional.map().orElse()로 HTTP 응답 직접 생성
     * - 2단계: 예외를 던지고 Controller에서 HTTP 처리
     *   → Service는 HTTP 몰라도 됨 (관심사 분리)
     *
     * @param id 조회할 유저의 id
     * @return 유저 정보를 담은 응답 DTO
     * @throws UserNotFoundException id에 해당하는 유저가 없을 때
     */
    public UserResponse getUserById(Long id) {
        // repository.findById(id):
        // - 성공: Optional.of(user)
        // - 실패: Optional.empty()

        // orElseThrow(() -> ...):
        // - Optional에 값이 있으면: User 반환
        // - Optional이 비어있으면: 람다 실행 → 예외 발생
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Domain → DTO 변환 후 반환
        return UserResponse.from(user);
    }

    /**
     * 특정 유저 정보 수정
     *
     * 처리 흐름:
     * 1. 입력 DTO 유효성 검증
     * 2. Repository에 수정 요청
     * 3. 수정 실패 시 (유저 없음) 예외 발생
     * 4. Domain → DTO 변환
     *
     * 이중 검증:
     * - 입력값 검증: name 필드 필수 체크
     * - 존재 여부 검증: 해당 id의 유저가 있는지 체크
     *
     * Optional 활용:
     * - updateById()가 Optional<User> 반환
     * - 수정 성공 시 수정된 User 담김
     * - 대상 유저 없으면 Optional.empty() → 예외 발생
     *
     * @param id 수정할 유저의 id
     * @param request 수정할 데이터를 담은 DTO
     * @return 수정된 유저 정보를 담은 응답 DTO
     * @throws InvalidRequestException name이 유효하지 않을 때
     * @throws UserNotFoundException id에 해당하는 유저가 없을 때
     */
    public UserResponse updateUser(Long id, UserRequest request) {
        // 1. 비즈니스 규칙 검증
        if (!request.isValid()) {
            throw new InvalidRequestException("name 필드가 필요합니다");
        }

        // 2. Repository에 수정 요청 + 존재 여부 검증
        // repository.updateById(id, name):
        // - 성공: Optional.of(수정된 User)
        // - 실패(유저 없음): Optional.empty()
        User user = repository.updateById(id, request.getName())
                .orElseThrow(() -> new UserNotFoundException(id));

        // 3. Domain → DTO 변환
        return UserResponse.from(user);
    }

    /**
     * 특정 유저 삭제
     *
     * void 반환 이유:
     * - 삭제 성공 시 반환할 데이터 없음 (삭제된 객체는 필요 없음)
     * - 실패 시 예외 발생하므로 boolean도 필요 없음
     *
     * Repository의 boolean 활용:
     * - deleteById()가 true/false 반환
     * - true: 삭제 성공 → 정상 종료
     * - false: 유저 없었음 → 예외 발생
     *
     * 예외 vs 반환값:
     * - 1단계: boolean 반환 후 Controller에서 분기 처리
     * - 2단계: 예외 발생으로 실패 케이스 명확히 표현
     *   → 호출부에서 try-catch 또는 예외 전파로 처리
     *
     * @param id 삭제할 유저의 id
     * @throws UserNotFoundException id에 해당하는 유저가 없을 때
     */
    public void deleteUser(Long id) {
        // Repository에 삭제 요청
        boolean deleted = repository.deleteById(id);

        // 삭제 실패 시 예외 발생
        // Controller에서 이 예외를 잡아서 404 Not Found 반환
        if (!deleted) {
            throw new UserNotFoundException(id);
        }
        // 삭제 성공 시 정상 종료 (반환값 없음)
    }
}