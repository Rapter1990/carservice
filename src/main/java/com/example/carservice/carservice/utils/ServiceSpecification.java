package com.example.carservice.carservice.utils;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class for defining dynamic JPA specifications for querying {@link ServiceEntity}.
 * Used for filtering service records based on criteria such as car ID or status.
 */
@UtilityClass
public class ServiceSpecification {

    /**
     * Creates a specification to filter {@link ServiceEntity} records by car ID.
     *
     * @param carId the ID of the car
     * @return a {@link Specification} for filtering services by car ID
     */
    public static Specification<ServiceEntity> hasCarId(String carId) {
        return (root, query, cb) ->
                cb.equal(root.get("car").get("id"), carId);
    }

    /**
     * Creates a specification to filter {@link ServiceEntity} records by service status.
     *
     * @param status the status of the service (e.g., COMPLETED, IN_PROGRESS)
     * @return a {@link Specification} for filtering services by status
     */
    public static Specification<ServiceEntity> hasStatus(ServiceStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}

