package com.example.carservice.carservice.utils;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ServiceSpecification {

    public static Specification<ServiceEntity> hasCarId(String carId) {
        return (root, query, cb) ->
                cb.equal(root.get("car").get("id"), carId);
    }

    public static Specification<ServiceEntity> hasStatus(ServiceStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}

