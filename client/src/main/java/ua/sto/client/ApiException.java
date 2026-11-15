package ua.sto.client;

/** Помилка, яку повернув сервіс (статус HTTP + текст із JSON-відповіді). */
public class ApiException extends Exception {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
