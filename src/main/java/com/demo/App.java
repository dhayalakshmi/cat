package com.demo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class App {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Home: message can come from a ConfigMap (Set 8)
        server.createContext("/", ex -> {
            String msg = System.getenv().getOrDefault("APP_MESSAGE", "Hello from Kubernetes! (v1)");
            send(ex, 200, msg);
        });

        // Factorial: /factorial/5 (also writes history to a PVC path, Set 9)
        server.createContext("/factorial/", ex -> {
            try {
                String path = ex.getRequestURI().getPath();
                int n = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                long f = MathUtil.factorial(n);
                saveHistory("factorial(" + n + ") = " + f);
                send(ex, 200, "{\"number\":" + n + ",\"factorial\":" + f + "}");
            } catch (Exception e) {
                send(ex, 400, "Invalid input");
            }
        });

        // Greatest of three: /greatest?a=3&b=9&c=5
        server.createContext("/greatest", ex -> {
            try {
                Map<String, String> q = query(ex.getRequestURI().getQuery());
                int a = Integer.parseInt(q.getOrDefault("a", "0"));
                int b = Integer.parseInt(q.getOrDefault("b", "0"));
                int c = Integer.parseInt(q.getOrDefault("c", "0"));
                send(ex, 200, "{\"greatest\":" + MathUtil.greatest(a, b, c) + "}");
            } catch (Exception e) {
                send(ex, 400, "Invalid input");
            }
        });

        // Health: readiness/liveness probe (Set 10)
        server.createContext("/health", ex -> send(ex, 200, "OK"));

        server.start();
        System.out.println("Server started on port 8080");
    }

    private static void saveHistory(String line) {
        String file = System.getenv("HISTORY_FILE"); // e.g. /data/history.txt
        if (file == null) return;
        try {
            Files.writeString(Paths.get(file), line + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) { }
    }

    private static Map<String, String> query(String q) {
        Map<String, String> map = new HashMap<>();
        if (q == null) return map;
        for (String pair : q.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

    private static void send(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }
}
