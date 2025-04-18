package com.example.carservice.carservice.model.dto.request.services;

import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.carservice.utils.ServiceSpecification;
import com.example.carservice.common.model.dto.specification.Filterable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@Getter
@Setter
public class ListServiceRequest implements Filterable<ServiceEntity> {

    /**
     * The filter criteria for service search.
     */
    private Filter filter;

    /**
     * Inner class representing the filter criteria for service search.
     */
    @Getter
    @Setter
    public static class Filter {

        private Optional<String> carId = Optional.empty();
        private Optional<ServiceStatus> status = Optional.empty();
    }

    /**
     * Converts the filter criteria into a Spring Data JPA Specification for querying services.
     *
     * @return A {@link Specification} representing the filter criteria for querying services.
     * @see ServiceSpecification
     */
    @Override
    public Specification<ServiceEntity> toSpecification() {
        final Specification<ServiceEntity>[] specification = new Specification[]{Specification.where(null)};

        if (filter != null) {
            filter.getCarId().ifPresent(carId ->
                    specification[0] = specification[0].and(ServiceSpecification.hasCarId(carId)));

            filter.getStatus().ifPresent(status ->
                    specification[0] = specification[0].and(ServiceSpecification.hasStatus(status)));
        }

        return specification[0];
    }
}

