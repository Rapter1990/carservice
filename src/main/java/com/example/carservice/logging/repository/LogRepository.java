package com.example.carservice.logging.repository;

import com.example.carservice.logging.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<LogEntity,String> {

}
