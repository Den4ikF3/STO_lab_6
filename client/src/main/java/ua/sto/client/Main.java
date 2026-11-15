package ua.sto.client;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Консольний клієнт REST-сервісу замовлень СТО.
 * Запуск: java ua.sto.client.Main [адреса сервісу]   (за замовчуванням http://localhost:8080/api/orders)
 */
public class Main {

    private static Scanner in;

    public static void main(String[] args) {
        String url = args.length > 0 ? args[0] : "http://localhost:8080/api/orders";
        OrderApiClient api = new OrderApiClient(url);
        in = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("Клієнт сервісу замовлень СТО. Адреса: " + url);
        while (true) {
            System.out.println();
            System.out.println("1 - Показати всі замовлення");
            System.out.println("2 - Знайти замовлення за ID");
            System.out.println("3 - Додати замовлення");
            System.out.println("4 - Змінити замовлення");
            System.out.println("5 - Видалити замовлення");
            System.out.println("0 - Вихід");
            System.out.print("Ваш вибір: ");
            if (!in.hasNextLine()) {
                return;
            }
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> showAll(api);
                    case "2" -> {
                        System.out.println(Order.header());
                        System.out.println(api.getById(readLong("ID: ")));
                    }
                    case "3" -> {
                        Order created = api.create(readOrder(null));
                        System.out.println("Створено. Сервер присвоїв ID = " + created.id());
                    }
                    case "4" -> update(api);
                    case "5" -> {
                        long id = readLong("ID для видалення: ");
                        api.delete(id);
                        System.out.println("Замовлення №" + id + " видалено.");
                    }
                    case "0" -> {
                        System.out.println("До побачення!");
                        return;
                    }
                    default -> System.out.println("Невідомий пункт меню.");
                }
            } catch (ApiException e) {
                System.out.println("Помилка сервісу (HTTP " + e.getStatus() + "): " + e.getMessage());
            } catch (ConnectException e) {
                System.out.println("Не вдалося підключитися до сервісу. Запущено сервер?");
            } catch (IOException e) {
                System.out.println("Помилка зв'язку: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void showAll(OrderApiClient api) throws IOException, InterruptedException, ApiException {
        List<Order> orders = api.getAll();
        if (orders.isEmpty()) {
            System.out.println("Замовлень немає.");
            return;
        }
        System.out.println(Order.header());
        orders.forEach(System.out::println);
    }

    private static void update(OrderApiClient api) throws IOException, InterruptedException, ApiException {
        long id = readLong("ID для зміни: ");
        Order current = api.getById(id); // якщо запису немає - сервер поверне 404
        System.out.println("Поточні дані:");
        System.out.println(Order.header());
        System.out.println(current);
        System.out.println("Введіть нові значення (Enter - залишити без змін):");
        Order updated = api.update(id, readOrder(current));
        System.out.println("Оновлено:");
        System.out.println(updated);
    }

    /** Зчитує дані замовлення; якщо defaults != null, порожній ввід залишає старе значення. */
    private static Order readOrder(Order d) {
        String client = readString("Клієнт", d == null ? null : d.clientName());
        String car = readString("Автомобіль", d == null ? null : d.carModel());
        int mileage = (int) readNumber("Пробіг, км", d == null ? null : d.mileageKm().doubleValue());
        double price = readNumber("Вартість, грн", d == null ? null : d.price());
        String date = readDate("Дата прийому (рррр-мм-дд)", d == null ? LocalDate.now().toString() : d.acceptedDate());
        return new Order(d == null ? null : d.id(), client, car, mileage, price, date);
    }

    private static String readString(String label, String def) {
        System.out.print(label + (def == null ? "" : " [" + def + "]") + ": ");
        String line = in.nextLine().trim();
        return line.isEmpty() && def != null ? def : line;
    }

    private static double readNumber(String label, Double def) {
        while (true) {
            String shown = def == null ? "" : (def == Math.floor(def) ? String.valueOf(def.longValue()) : String.valueOf(def));
            System.out.print(label + (def == null ? "" : " [" + shown + "]") + ": ");
            String line = in.nextLine().trim().replace(',', '.');
            if (line.isEmpty() && def != null) {
                return def;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Введіть число.");
            }
        }
    }

    private static String readDate(String label, String def) {
        while (true) {
            System.out.print(label + " [" + def + "]: ");
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                return def;
            }
            try {
                return LocalDate.parse(line).toString();
            } catch (DateTimeParseException e) {
                System.out.println("Невірна дата. Приклад: 2026-11-13");
            }
        }
    }

    private static long readLong(String label) {
        while (true) {
            System.out.print(label);
            String line = in.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Введіть ціле число.");
            }
        }
    }
}
