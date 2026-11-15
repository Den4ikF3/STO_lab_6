package ua.sto.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.sto.boot.model.ServiceOrder;

/**
 * Репозиторій Spring Data JPA. Реалізацію (save, findAll, findById, deleteById, count...)
 * Spring генерує автоматично - писати SQL і код DAO вручну не потрібно.
 */
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
}
