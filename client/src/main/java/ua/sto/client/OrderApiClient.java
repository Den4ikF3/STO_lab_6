package ua.sto.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Клієнт REST API: перетворює виклики методів на HTTP-запити до сервісу замовлень. */
public class OrderApiClient {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final String baseUrl;

    public OrderApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    /** GET /api/orders */
    public List<Order> getAll() throws IOException, InterruptedException, ApiException {
        Object body = Json.parse(send("GET", "", null, 200));
        List<Order> result = new ArrayList<>();
        for (Object o : (List<?>) body) {
            result.add(Order.fromMap((Map<?, ?>) o));
        }
        return result;
    }

    /** GET /api/orders/{id} */
    public Order getById(long id) throws IOException, InterruptedException, ApiException {
        return Order.fromMap((Map<?, ?>) Json.parse(send("GET", "/" + id, null, 200)));
    }

    /** POST /api/orders */
    public Order create(Order order) throws IOException, InterruptedException, ApiException {
        return Order.fromMap((Map<?, ?>) Json.parse(send("POST", "", order.toJson(), 201)));
    }

    /** PUT /api/orders/{id} */
    public Order update(long id, Order order) throws IOException, InterruptedException, ApiException {
        return Order.fromMap((Map<?, ?>) Json.parse(send("PUT", "/" + id, order.toJson(), 200)));
    }

    /** DELETE /api/orders/{id} */
    public void delete(long id) throws IOException, InterruptedException, ApiException {
        send("DELETE", "/" + id, null, 204);
    }

    private String send(String method, String path, String json, int expectedStatus)
            throws IOException, InterruptedException, ApiException {
        HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json");
        if (json != null) {
            rb.header("Content-Type", "application/json; charset=UTF-8")
                    .method(method, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        } else {
            rb.method(method, HttpRequest.BodyPublishers.noBody());
        }
        HttpResponse<String> resp = http.send(rb.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() != expectedStatus) {
            throw new ApiException(resp.statusCode(), describeError(resp));
        }
        return resp.body();
    }

    /** Формує зрозуміле повідомлення з JSON-відповіді з помилкою. */
    private String describeError(HttpResponse<String> resp) {
        try {
            Object parsed = Json.parse(resp.body());
            if (parsed instanceof Map<?, ?> m) {
                StringBuilder sb = new StringBuilder(String.valueOf(m.get("error")));
                if (m.get("details") instanceof Map<?, ?> details) {
                    details.forEach((k, v) -> sb.append("\n   - ").append(k).append(": ").append(v));
                }
                return sb.toString();
            }
        } catch (RuntimeException ignored) {
            // тіло не є JSON - покажемо як є
        }
        return resp.body() == null || resp.body().isBlank() ? "Порожня відповідь" : resp.body();
    }
}
