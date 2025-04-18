package com.example.carservice.carservice.model.entity;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a car.
 * Each car is associated with a user and may have multiple service records.
 * This entity includes attributes like license plate, model, brand, and status.
 *
 * @see UserEntity
 * @see ServiceEntity
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "CARS")
public class CarEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "LICENSE_PLATE", unique = true, nullable = false)
    private String licensePlate;

    @Column(name = "MODEL", nullable = false)
    private String model;

    @Column(name = "BRAND", nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CarStatus status = CarStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceEntity> services = new ArrayList<>();

}
