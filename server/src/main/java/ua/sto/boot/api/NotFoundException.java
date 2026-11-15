package ua.sto.boot.api;

/** Запис не знайдено - перетворюється на HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(Long id) {
        super("Замовлення №" + id + " не знайдено");
    }
}
