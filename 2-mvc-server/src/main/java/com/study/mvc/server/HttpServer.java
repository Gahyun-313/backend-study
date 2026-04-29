package com.study.mvc.server;

import com.study.mvc.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP 서버의 진입점 - 클라이언트 연결을 받아 스레드로 처리
 *
 * 역할:
 * 1. 지정된 포트로 ServerSocket 생성
 * 2. 클라이언트 연결 대기 (accept)
 * 3. 연결마다 새 스레드로 RequestHandler 실행
 *
 * 학습 포인트:
 * - 1단계와 동일한 서버 구조
 * - accept()는 블로킹 방식 (클라이언트 연결까지 대기)
 * - 스레드 풀로 동시 요청 처리 (최대 10개)
 *
 * 멀티스레드 처리 방식:
 * - 요청 1개 = 스레드 1개
 * - 동시에 10개 요청까지 처리 가능
 * - 11번째 요청은 큐에서 대기 (스레드가 빌 때까지)
 */
public class HttpServer {

    private final int port;                        // 서버가 실행될 포트 번호
    private final ExecutorService threadPool;      // 스레드 풀 (요청 처리용)

    /**
     * HttpServer 생성자
     *
     * @param port 서버가 실행될 포트 (보통 8080)
     *
     * 스레드 풀 생성:
     * - newFixedThreadPool(10): 고정 크기 스레드 풀
     * - 최대 10개의 스레드가 동시에 요청 처리
     * - 11번째 요청부터는 큐에서 대기
     *
     * 왜 스레드 풀을 사용하나?
     * - new Thread() 방식은 요청마다 스레드 생성/삭제 (비용 큼)
     * - 스레드 풀은 미리 만들어둔 스레드를 재사용 (효율적)
     * - 동시 요청 수를 제한해서 서버 과부하 방지
     */
    public HttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    /**
     * 서버 시작 - 무한 루프로 클라이언트 연결 대기
     *
     * @throws IOException ServerSocket 생성 실패 시
     *
     * 처리 흐름:
     * 1. ServerSocket 생성 (지정된 포트로 바인딩)
     * 2. 시작 메시지 출력
     * 3. 무한 루프 진입:
     *    - accept()로 클라이언트 연결 대기 (블로킹)
     *    - 연결되면 Socket 객체 생성됨
     *    - threadPool에 RequestHandler 제출
     *    - 스레드가 RequestHandler.run() 실행
     *    - 다시 accept()로 돌아가서 다음 연결 대기
     *
     * accept()의 블로킹 특성:
     * - 클라이언트가 연결할 때까지 여기서 멈춤
     * - 연결되는 순간 즉시 다음 라인 실행
     * - while(true) 덕분에 계속 새 연결 받을 수 있음
     *
     * 왜 serverSocket을 닫지 않나?
     * - while(true)라서 절대 빠져나오지 못함
     * - Ctrl+C로 강제 종료 시 JVM이 자동으로 리소스 정리
     * - 실무에서는 Graceful Shutdown 구현 (종료 신호 받으면 정리 후 종료)
     */
    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("✅ Server started → http://localhost:" + port);
        System.out.println("   종료: Ctrl+C\n");

        // 무한 루프: 계속 클라이언트 연결 받음
        while (true) {
            // accept(): 클라이언트 연결까지 블로킹 (대기)
            Socket clientSocket = serverSocket.accept();

            // 스레드 풀에 작업 제출 (비동기 실행)
            // → 메인 스레드는 즉시 다시 accept()로 돌아감
            threadPool.submit(new RequestHandler(clientSocket));
        }
    }
}