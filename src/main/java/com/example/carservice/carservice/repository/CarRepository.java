package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link CarEntity} persistence operations.
 * Extends {@link JpaRepository} for standard CRUD and paging functionality.
 */
public interface CarRepository extends JpaRepository<CarEntity, String> {

    /**
     * Checks if a car with the given license plate already exists.
     *
     * @param licensePlate the license plate to check
     * @return {@code true} if a car exists with the given plate, {@code false} otherwise
     */
    boolean existsByLicensePlate(String licensePlate);

    /**
     * Retrieves a paginated list of cars belonging to a specific user and matching the given status.
     *
     * @param userId   the ID of the user
     * @param status   the status of the cars to filter by
     * @param pageable pagination information
     * @return a page of {@link CarEntity} objects
     */
    Page<CarEntity> findByUserIdAndStatus(String userId, CarStatus status, Pageable pageable);

    /**
     * Retrieves a paginated list of cars filtered by status.
     *
     * @param status   the status of the cars
     * @param pageable pagination information
     * @return a page of {@link CarEntity} objects
     */
    Page<CarEntity> findByStatus(CarStatus status, Pageable pageable);

}
