package ua.sto.client;

import java.util.Locale;
import java.util.Map;

/** Замовлення на стороні клієнта (відповідає JSON-об'єкту, який повертає сервіс). */
public record Order(Long id, String clientName, String carModel, Integer mileageKm, Double price, String acceptedDate) {

    /** Створення об'єкта з результату розбору JSON. */
    public static Order fromMap(Map<?, ?> m) {
        return new Order(
                num(m.get("id")) == null ? null : num(m.get("id")).longValue(),
                (String) m.get("clientName"),
                (String) m.get("carModel"),
                num(m.get("mileageKm")) == null ? null : num(m.get("mileageKm")).intValue(),
                num(m.get("price")),
                (String) m.get("acceptedDate"));
    }

    private static Double num(Object o) {
        return o == null ? null : ((Number) o).doubleValue();
    }

    /** JSON для POST/PUT (id не передається - його задає сервер або URL). */
    public String toJson() {
        return "{"
                + "\"clientName\":" + Json.quote(clientName) + ","
                + "\"carModel\":" + Json.quote(carModel) + ","
                + "\"mileageKm\":" + mileageKm + ","
                + "\"price\":" + String.format(Locale.US, "%.2f", price) + ","
                + "\"acceptedDate\":" + Json.quote(acceptedDate)
                + "}";
    }

    public static String header() {
        return String.format("%-4s %-22s %-20s %10s %12s  %-10s", "ID", "Клієнт", "Автомобіль", "Пробіг,км", "Вартість,грн", "Дата");
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%-4d %-22s %-20s %10d %12.2f  %-10s",
                id, clientName, carModel, mileageKm, price, acceptedDate);
    }
}
