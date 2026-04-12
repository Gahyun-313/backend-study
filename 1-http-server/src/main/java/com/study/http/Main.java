package com.study.http;

import com.study.http.server.HttpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        new HttpServer(8080).start();
    }
}