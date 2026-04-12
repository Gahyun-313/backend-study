package com.study.http.server;

import com.study.http.handler.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [1] accept()는 요청이 올 때까지 블로킹됨
 *      -> "서버는 요청을 기다리는 프로그램"
 * [2] 스레드풀 사용 이유
 *      요청마다 new Thread() 생성 시 수천 요청에서 OOM 위헙
 *      -> 10개 스레드를 재사용하여 동시 요청을 안전하게 처리
 */
public class HttpServer {

    private final int port;
    private final ExecutorService threadPool;

    public HttpServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("✅ Server started on port " + port + " -> http://localhost:" + port + "/");
        System.out.println("    종료: Ctrl+C\n");

        while (true) {
            Socket clientSocket = serverSocket.accept(); // 블로킹
            threadPool.submit(new RequestHandler(clientSocket));
        }
    }
}
