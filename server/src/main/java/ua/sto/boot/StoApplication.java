package ua.sto.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Точка входу застосунку Spring Boot (CRUD для таблиці замовлень СТО). */
@SpringBootApplication
public class StoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoApplication.class, args);
    }
}
