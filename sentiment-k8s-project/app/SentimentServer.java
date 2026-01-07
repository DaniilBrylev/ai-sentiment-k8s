import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;

public class SentimentServer {
    private static final int PORT = 8080;
    private static final List<String> POSITIVE = List.of(
            "good", "great", "love", "awesome", "отлично", "супер"
    );
    private static final List<String> NEGATIVE = List.of(
            "bad", "hate", "terrible", "ужасно", "плохо"
    );

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/health", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                respondText(exchange, 405, "Method Not Allowed");
                return;
            }
            respondText(exchange, 200, "OK");
        });

        server.createContext("/api/sentiment", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                respondText(exchange, 405, "Method Not Allowed");
                return;
            }
            String text = getQueryParam(exchange.getRequestURI().getRawQuery(), "text");
            if (text == null) {
                respondJson(exchange, 400, "{\"error\":\"text query parameter is required\"}");
                return;
            }
            SentimentResult result = analyze(text);
            respondJson(exchange, 200, result.toJson());
        });

        server.createContext("/metrics", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                respondText(exchange, 405, "Method Not Allowed");
                return;
            }
            String body = "# HELP sentiment_requests_total Total requests handled (mock)\n"
                    + "# TYPE sentiment_requests_total counter\n"
                    + "sentiment_requests_total 1\n";
            respondText(exchange, 200, body, "text/plain; version=0.0.4; charset=utf-8");
        });

        server.createContext("/", exchange -> respondText(exchange, 404, "Not Found"));

        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Sentiment server listening on port " + PORT);
    }

    private static SentimentResult analyze(String text) {
        String normalized = text.toLowerCase(Locale.ROOT);
        boolean hasPositive = containsAny(normalized, POSITIVE);
        boolean hasNegative = containsAny(normalized, NEGATIVE);

        if (hasPositive && !hasNegative) {
            return new SentimentResult(text, "positive", 0.87);
        }
        if (hasNegative && !hasPositive) {
            return new SentimentResult(text, "negative", -0.76);
        }
        return new SentimentResult(text, "neutral", 0.00);
    }

    private static boolean containsAny(String text, List<String> tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private static String getQueryParam(String rawQuery, String key) {
        if (rawQuery == null || rawQuery.isEmpty()) {
            return null;
        }
        Map<String, String> params = new LinkedHashMap<>();
        String[] pairs = rawQuery.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String name = urlDecode(parts[0]);
            String value = parts.length > 1 ? urlDecode(parts[1]) : "";
            params.put(name, value);
        }
        return params.get(key);
    }

    private static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static void respondJson(HttpExchange exchange, int status, String body) throws IOException {
        respondText(exchange, status, body, "application/json; charset=utf-8");
    }

    private static void respondText(HttpExchange exchange, int status, String body) throws IOException {
        respondText(exchange, status, body, "text/plain; charset=utf-8");
    }

    private static void respondText(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static final class SentimentResult {
        private final String text;
        private final String sentiment;
        private final double score;

        private SentimentResult(String text, String sentiment, double score) {
            this.text = text;
            this.sentiment = sentiment;
            this.score = score;
        }

        private String toJson() {
            String escapedText = jsonEscape(text);
            String escapedSentiment = jsonEscape(sentiment);
            String scoreValue = String.format(Locale.US, "%.2f", score);
            return "{\"text\":\"" + escapedText + "\",\"sentiment\":\"" + escapedSentiment
                    + "\",\"score\":" + scoreValue + "}";
        }

        private static String jsonEscape(String value) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '\\':
                        builder.append("\\\\");
                        break;
                    case '"':
                        builder.append("\\\"");
                        break;
                    case '\n':
                        builder.append("\\n");
                        break;
                    case '\r':
                        builder.append("\\r");
                        break;
                    case '\t':
                        builder.append("\\t");
                        break;
                    default:
                        builder.append(c);
                }
            }
            return builder.toString();
        }
    }
}