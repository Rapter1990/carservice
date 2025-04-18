package com.example.carservice.carservice.repository;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for managing {@link ServiceEntity} persistence operations.
 * Extends {@link JpaRepository} for CRUD operations and {@link JpaSpecificationExecutor} for dynamic filtering.
 */
public interface ServiceRepository extends JpaRepository<ServiceEntity, String>, JpaSpecificationExecutor<ServiceEntity> {

    /**
     * Checks if a service exists with the given title.
     *
     * @param title the title of the service
     * @return {@code true} if a service with the given title exists, {@code false} otherwise
     */
    boolean existsByTitle(String title);

    /**
     * Retrieves a paginated list of services associated with a specific car ID.
     *
     * @param carId    the ID of the car
     * @param pageable pagination information
     * @return a page of {@link ServiceEntity} objects
     */
    Page<ServiceEntity> findByCarId(String carId, Pageable pageable);

}
