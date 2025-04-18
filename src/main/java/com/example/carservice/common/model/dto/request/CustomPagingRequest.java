package com.example.carservice.common.model.dto.request;

import com.example.carservice.common.model.CustomPaging;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Represents a custom paging request that wraps pagination information
 * and converts it to a Spring Data {@link Pageable} object.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CustomPagingRequest {

    private CustomPaging pagination;

    /**
     * Converts {@link CustomPaging} into a Spring {@link Pageable} instance.
     *
     * @return a {@link PageRequest} object containing the page number and size
     */
    public Pageable toPageable() {
        return PageRequest.of(
                Math.toIntExact(pagination.getPageNumber()),
                Math.toIntExact(pagination.getPageSize())
        );
    }

}
