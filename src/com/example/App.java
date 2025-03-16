package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) {
        try {
            // Создаем сервер на порту 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Добавляем обработчики маршрутов
            server.createContext("/", new RegHandler());
            server.createContext("/style.css", new CssHandler1());  // Обработчик для CSS
            server.createContext("/home.css", new CssHandler2());  // Обработчик для CSS
            server.createContext("/login", new LoginHandler());
            server.createContext("/home", new HomeHandler());
            server.createContext("/reg_button", new RegButtonHandler());
            server.createContext("/images", new ImageHandler());

            // Запускаем сервер
            server.setExecutor(null); // Используется дефолтный executor
            server.start();
            System.out.println("Сервер запущен на http://localhost:8080/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Обработчик для главной страницы
    static class RegHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Путь к файлу register.html
            Path path = Paths.get("resources", "register.html");

            // Проверяем, существует ли файл
            if (Files.exists(path)) {
                byte[] response = Files.readAllBytes(path);  // Читаем файл в байты
                exchange.sendResponseHeaders(200, response.length);  // Отправляем заголовки
                OutputStream os = exchange.getResponseBody();
                os.write(response);  // Отправляем содержимое файла
                os.close();
            } else {
                String response = "<html><body><h1>404 - Страница не найдена</h1></body></html>";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Путь к файлу register.html
            Path path = Paths.get("resources", "login.html");

            // Проверяем, существует ли файл
            if (Files.exists(path)) {
                byte[] response = Files.readAllBytes(path);  // Читаем файл в байты
                exchange.sendResponseHeaders(200, response.length);  // Отправляем заголовки
                OutputStream os = exchange.getResponseBody();
                os.write(response);  // Отправляем содержимое файла
                os.close();
            } else {
                String response = "<html><body><h1>404 - Страница не найдена</h1></body></html>";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Путь к файлу register.html
            Path path = Paths.get("resources", "home.html");

            // Проверяем, существует ли файл
            if (Files.exists(path)) {
                byte[] response = Files.readAllBytes(path);  // Читаем файл в байты
                exchange.sendResponseHeaders(200, response.length);  // Отправляем заголовки
                OutputStream os = exchange.getResponseBody();
                os.write(response);  // Отправляем содержимое файла
                os.close();
            } else {
                String response = "<html><body><h1>404 - Страница не найдена</h1></body></html>";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
    // Обработчик для CSS файла
    static class CssHandler1 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Path cssPath = Paths.get("resources", "style.css");

            if (!Files.exists(cssPath)) {
                String errorMessage = "CSS файл не найден!";
                exchange.sendResponseHeaders(404, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
                return;
            }

            byte[] cssBytes = Files.readAllBytes(cssPath);
            exchange.getResponseHeaders().set("Content-Type", "text/css");
            exchange.sendResponseHeaders(200, cssBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(cssBytes);
            }

        }
    }
    static class CssHandler2 implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Path cssPath = Paths.get("resources", "home.css");

            if (!Files.exists(cssPath)) {
                String errorMessage = "CSS файл не найден!";
                exchange.sendResponseHeaders(404, errorMessage.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorMessage.getBytes());
                }
                return;
            }

            byte[] cssBytes = Files.readAllBytes(cssPath);
            exchange.getResponseHeaders().set("Content-Type", "text/css");
            exchange.sendResponseHeaders(200, cssBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(cssBytes);
            }

        }
    }

    static class RegButtonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Если метод не POST, отправляем ошибку
                String response = "Метод не поддерживается!";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            try {
                // Получаем данные из запроса
                InputStream inputStream = exchange.getRequestBody();
                StringBuilder stringBuilder = new StringBuilder();
                int ch;
                while ((ch = inputStream.read()) != -1) {
                    stringBuilder.append((char) ch);
                }
                String requestBody = stringBuilder.toString();

                // Логируем полученные данные
                System.out.println("Получены данные формы: " + requestBody);

                // Разбираем параметры
                Map<String, String> data = parseFormData(requestBody);

                String login = data.getOrDefault("login", null);
                String password1 = data.getOrDefault("password1", null);
                String password2 = data.getOrDefault("password2", null);

                // Вывод в консоль для отладки
                System.out.println("Логин: " + login);
                System.out.println("Пароль 1: " + password1);
                System.out.println("Пароль 2: " + password2);

                exchange.getResponseHeaders().set("Location", "/login");
                exchange.sendResponseHeaders(302, -1); // 302 Found -> Редирект
                exchange.close();

            } catch (Exception e){
                String errorResponse = "<html><body><h1>Ошибка сервера!</h1></body></html>";
                exchange.sendResponseHeaders(500, errorResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorResponse.getBytes(StandardCharsets.UTF_8));
                }
            }
        }

        // Разбор формы с декодированием данных
        private Map<String, String> parseFormData(String formData){
            Map<String, String> data = new HashMap<>();
            String[] pairs = formData.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    data.put(key, value);
                }
            }
            return data;
        }
    }
    static class ImageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = "resources/images" + exchange.getRequestURI().getPath().replace("/images", "");
            Path path = Paths.get(filePath);

            if (Files.exists(path)) {
                byte[] imageBytes = Files.readAllBytes(path);

                // Определяем MIME-тип по расширению
                String contentType = "image/png";
                if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (filePath.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (filePath.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                }

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, imageBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(imageBytes);
                }
            } else {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().close();
            }
        }
    }


}

