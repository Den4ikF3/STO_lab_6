package ua.sto.boot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Сутність "Замовлення на обслуговування" (таблиця service_order). */
@Entity
@Table(name = "service_order")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Вкажіть клієнта")
    @Size(max = 100, message = "Не більше 100 символів")
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;

    @NotBlank(message = "Вкажіть модель автомобіля")
    @Size(max = 100, message = "Не більше 100 символів")
    @Column(name = "car_model", nullable = false, length = 100)
    private String carModel;

    @NotNull(message = "Вкажіть пробіг")
    @Min(value = 0, message = "Пробіг не може бути від'ємним")
    @Column(name = "mileage_km")
    private Integer mileageKm;

    @NotNull(message = "Вкажіть вартість")
    @DecimalMin(value = "0.0", message = "Вартість не може бути від'ємною")
    @Column(name = "price")
    private Double price;

    @NotNull(message = "Вкажіть дату прийому")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "accepted_date")
    private LocalDate acceptedDate;

    public ServiceOrder() {
    }

    public ServiceOrder(String clientName, String carModel, Integer mileageKm, Double price, LocalDate acceptedDate) {
        this.clientName = clientName;
        this.carModel = carModel;
        this.mileageKm = mileageKm;
        this.price = price;
        this.acceptedDate = acceptedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Integer getMileageKm() {
        return mileageKm;
    }

    public void setMileageKm(Integer mileageKm) {
        this.mileageKm = mileageKm;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDate acceptedDate) {
        this.acceptedDate = acceptedDate;
    }
}
