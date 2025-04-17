package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, String> {

    boolean existsByLicensePlate(String licensePlate);

    Page<CarEntity> findByUserIdAndStatus(String userId, CarStatus status, Pageable pageable);

    Page<CarEntity> findByStatus(CarStatus status, Pageable pageable);

}
