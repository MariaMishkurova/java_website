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
            /* Создаем объект класса сервер с сокетом(InetSocketAddress) на порту 8080
            InetSocketAddress - передача право компьютеру создать сокет (установить соединение по этому порту)
            после этого программист может отправлять данные просто в сокет и принимать их оттуда */
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
            server.start();
            System.out.println("http://localhost:8080/");
        /*IOException возникает, когда операция ввода/вывода завершается неудачей или прерывается. Это может
        произойти во время таких сценариев, как чтение или запись в файл, сетевые коммуникации или при взаимодействии
        с файловой системой.
        */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Обработчик для cтраницы регистрации

    static class RegHandler implements HttpHandler {
        //Метод вызывается сервером каждый раз, когда поступает HTTP-запрос.
        //Объект HttpExchange содержит всю информацию о запросе и позволяет отправить ответ клиенту.
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Путь к файлу register.html
            Path path = Paths.get("resources", "register.html");

            // Проверяем, существует ли файл по данному пути
            if (Files.exists(path)) {
                byte[] response = Files.readAllBytes(path);  // Читаем файл в байты
                /*Заголовки - данные, которые передаются
                Перый аргумент - это числовой код состояния, который сообщает клиенту, как обработан запрос.
                Примеры HTTP-кодов:
                200 OK – запрос выполнен успешно.
                404 Not Found – запрашиваемый ресурс не найден.
                500 Internal Server Error – ошибка на сервере.
                Второй аргумент - Если response.length, то сервер отправит точное количество байтов, указанное в этом параметре.
                Если указано -1, сервер отправит "chunked transfer encoding" (фрагментированную передачу),
                что полезно, когда заранее неизвестен размер ответа.*/
                exchange.sendResponseHeaders(200, response.length);
                //получаем поток http запроса
                OutputStream os = exchange.getResponseBody();
                // Отправляем содержимое файла в поток
                os.write(response);
                //закрываем этот поток
                os.close();
            } else {
                //то же самое, но другой код состояния и другой ответ
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
            //устанавливает заголовок с типом данных. При отправке это указать нельзя, потому что,
            //видите ли, sendResponseHeaders принимает только код состояния и длину
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
//обработка кнопки зарегистрироваться
    static class RegButtonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Получаем данные из запроса (что ввели)
                InputStream inputStream = exchange.getRequestBody();
                //Создаем гибкую строку, которую можно менять
                StringBuilder stringBuilder = new StringBuilder();
                int ch;// - байт
                while ((ch = inputStream.read()) != -1) {
                    stringBuilder.append((char) ch);//преобразование байта в символ
                }
                String requestBody = stringBuilder.toString();
                // Разбираем параметры
                Map<String, String> data = parseFormData(requestBody);
                String login = data.getOrDefault("login", null);
                String password1 = data.getOrDefault("password1", null);
                String password2 = data.getOrDefault("password2", null);
                //Location - редирект
                exchange.getResponseHeaders().set("Location", "/home");
                exchange.sendResponseHeaders(302, -1);
                exchange.close();

            } catch (Exception e){
                String errorResponse = "<html><body><h1>Ошибка сервера!</h1></body></html>";
                exchange.sendResponseHeaders(500, errorResponse.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errorResponse.getBytes());
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

