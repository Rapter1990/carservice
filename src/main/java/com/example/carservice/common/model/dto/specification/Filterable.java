package com.example.carservice.common.model.dto.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * Interface for creating specifications to filter entities of type {@code T}.
 * Implementations should provide a method to convert the filter criteria into a
 * Spring Data JPA {@link Specification} to be used in querying the database.
 *
 * @param <T> The type of entity to which the filter criteria will be applied.
 */
public interface Filterable<T> {

    /**
     * Converts the filter criteria into a Spring Data JPA Specification.
     *
     * @return A {@link Specification} representing the filter criteria for entities of type {@code T}.
     */
    Specification<T> toSpecification();

}
