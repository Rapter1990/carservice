package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, String> {

    boolean existsByLicensePlate(String licensePlate);

}
