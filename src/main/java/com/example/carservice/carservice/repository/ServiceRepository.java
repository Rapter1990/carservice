package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String>, JpaSpecificationExecutor<ServiceEntity> {

    boolean existsByTitle(String title);

    Page<ServiceEntity> findByCarId(String carId, Pageable pageable);

}
