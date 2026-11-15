package ua.sto.boot.api;

import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.sto.boot.model.ServiceOrder;
import ua.sto.boot.repository.ServiceOrderRepository;

import java.net.URI;
import java.util.List;

/**
 * REST API для замовлень СТО. Дані передаються у форматі JSON.
 *
 *   GET    /api/orders        - список усіх замовлень             (200)
 *   GET    /api/orders/{id}   - одне замовлення                   (200 / 404)
 *   POST   /api/orders        - створити замовлення               (201 + Location / 400)
 *   PUT    /api/orders/{id}   - повністю оновити замовлення       (200 / 400 / 404)
 *   DELETE /api/orders/{id}   - видалити замовлення               (204 / 404)
 */
@RestController
@RequestMapping("/api/orders")
public class ServiceOrderRestController {

    private final ServiceOrderRepository repository;

    public ServiceOrderRestController(ServiceOrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ServiceOrder> getAll() {
        return repository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public ServiceOrder getOne(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ServiceOrder> create(@Valid @RequestBody ServiceOrder order) {
        order.setId(null); // id генерує БД
        ServiceOrder saved = repository.save(order);
        return ResponseEntity.created(URI.create("/api/orders/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ServiceOrder update(@PathVariable Long id, @Valid @RequestBody ServiceOrder order) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(id);
        }
        order.setId(id);
        return repository.save(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
