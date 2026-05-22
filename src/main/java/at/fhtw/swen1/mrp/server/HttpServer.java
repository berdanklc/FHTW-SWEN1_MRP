package at.fhtw.swen1.mrp.server;

import at.fhtw.swen1.mrp.controller.AuthController;
import at.fhtw.swen1.mrp.controller.FavoriteController;
import at.fhtw.swen1.mrp.controller.MediaController;
import at.fhtw.swen1.mrp.controller.RatingController;
import at.fhtw.swen1.mrp.controller.UserController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class HttpServer {
    private final com.sun.net.httpserver.HttpServer server;
    private final AuthController authController;
    private final FavoriteController favoriteController;
    private final MediaController mediaController;
    private final RatingController ratingController;
    private final UserController userController;
    private final int port;

    public HttpServer() throws IOException {
        this.port = resolvePort();
        this.server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        this.authController = new AuthController();
        this.favoriteController = new FavoriteController();
        this.mediaController = new MediaController();
        this.ratingController = new RatingController();
        this.userController = new UserController();

        configureRoutes();
        server.setExecutor(Executors.newFixedThreadPool(10));
    }

    private int resolvePort() {
        String portFromEnvironment = System.getenv("PORT");

        if (portFromEnvironment == null || portFromEnvironment.isBlank()) {
            portFromEnvironment = System.getenv("WEBSITES_PORT");
        }

        if (portFromEnvironment == null || portFromEnvironment.isBlank()) {
            return 8080;
        }

        try {
            return Integer.parseInt(portFromEnvironment);
        } catch (NumberFormatException e) {
            System.out.println("Invalid PORT value. Falling back to port 8080.");
            return 8080;
        }
    }

    private void configureRoutes() {
        // Health check for local and Azure testing
        server.createContext("/api/health", exchange -> {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            sendJsonResponse(exchange, 200, "{\"status\":\"UP\",\"service\":\"Media Ratings Platform\"}");
        });

        // Authentifizierung
        server.createContext("/api/users/register", authController::handleRegister);
        server.createContext("/api/users/login", authController::handleLogin);

        // Leaderboard
        server.createContext("/api/leaderboard", userController::handleLeaderboard);

        // User Routes inkl. Favorites
        server.createContext("/api/users", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/api/users/[^/]+/favorites")) {
                favoriteController.handleFavorite(exchange);
            } else {
                userController.handleUser(exchange);
            }
        });

        // Media Routes inkl. Rating und Favorite
        server.createContext("/api/media", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.matches("/api/media/\\d+/rate")) {
                ratingController.handleRating(exchange);
            } else if (path.matches("/api/media/\\d+/favorite")) {
                favoriteController.handleFavorite(exchange);
            } else {
                mediaController.handleMedia(exchange);
            }
        });

        server.createContext("/api/ratings", ratingController::handleRating);
    }

    private void sendJsonResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }

    public void start() {
        server.start();
        System.out.println("Server started on port " + port);
        System.out.println("Media Ratings Platform server is running.");
    }

    public void stop() {
        server.stop(0);
        System.out.println("Server stopped");
    }
}
