package ua.sto.boot.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.sto.boot.model.ServiceOrder;
import ua.sto.boot.repository.ServiceOrderRepository;

import java.time.LocalDate;
import java.util.List;

/** При першому запуску додає демонстраційні записи, якщо таблиця порожня. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ServiceOrderRepository repository;

    public DataInitializer(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new ServiceOrder("Іваненко Олег", "Toyota Camry", 85000, 2800.0, LocalDate.of(2026, 10, 12)),
                new ServiceOrder("Петренко Марія", "Volkswagen Golf", 132000, 1650.5, LocalDate.of(2026, 10, 20)),
                new ServiceOrder("Коваль Андрій", "Skoda Octavia", 47000, 950.0, LocalDate.of(2026, 11, 3)),
                new ServiceOrder("ТОВ \"Логістик\"", "Ford Transit", 210000, 5400.0, LocalDate.of(2026, 11, 10))
        ));
    }
}
