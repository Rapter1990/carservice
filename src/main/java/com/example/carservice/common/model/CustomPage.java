package com.example.carservice.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A wrapper around Spring Data's {@link Page} to standardize API pagination responses.
 *
 * @param <T> the type of content in the page
 */
@Getter
@Builder
public class CustomPage<T> {

    private List<T> content;

    private Integer pageNumber;

    private Integer pageSize;

    private Long totalElementCount;

    private Integer totalPageCount;

    /**
     * Converts a Spring {@link Page} into a {@link CustomPage}, preserving pagination metadata.
     *
     * @param domainModels the content to be returned
     * @param page the source Spring page object
     * @param <C> the type of the response content
     * @param <X> the type of the original page content
     * @return a {@link CustomPage} instance
     */
    public static <C, X> CustomPage<C> of(final List<C> domainModels, final Page<X> page) {
        return CustomPage.<C>builder()
                .content(domainModels)
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPageCount(page.getTotalPages())
                .totalElementCount(page.getTotalElements())
                .build();
    }

}
