package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, String> {

}
