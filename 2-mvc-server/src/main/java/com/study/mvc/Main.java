package com.study.mvc;

import com.study.mvc.server.HttpServer;

public class Main {
    public static void main(String[] args) throws Exception {
        new HttpServer(8080).start();
    }
}