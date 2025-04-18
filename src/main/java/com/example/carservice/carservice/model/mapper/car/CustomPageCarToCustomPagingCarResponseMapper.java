package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting {@link CustomPage} of {@link Car} to a paginated {@link CustomPagingResponse} of {@link CarResponse}.
 */
@Mapper
public interface CustomPageCarToCustomPagingCarResponseMapper {

    CarToCarResponseMapper carToCarResponseMapper = Mappers.getMapper(CarToCarResponseMapper.class);

    /**
     * Converts a {@link CustomPage} of {@link Car} to {@link CustomPagingResponse} of {@link CarResponse}.
     *
     * @param carPage the domain model page
     * @return the API-friendly paginated response
     */
    default CustomPagingResponse<CarResponse> toPagingResponse(CustomPage<Car> carPage) {
        if (carPage == null) {
            return null;
        }

        return CustomPagingResponse.<CarResponse>builder()
                .content(toCarResponseList(carPage.getContent()))
                .totalElementCount(carPage.getTotalElementCount())
                .totalPageCount(carPage.getTotalPageCount())
                .pageNumber(carPage.getPageNumber())
                .pageSize(carPage.getPageSize())
                .build();
    }

    /**
     * Maps a list of {@link Car} to a list of {@link CarResponse}.
     *
     * @param cars domain model list
     * @return response list
     */
    default List<CarResponse> toCarResponseList(List<Car> cars) {
        if (cars == null) {
            return null;
        }

        return cars.stream()
                .map(carToCarResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Initializes the mapper using MapStruct's factory method.
     *
     * @return a singleton mapper instance
     */
    static CustomPageCarToCustomPagingCarResponseMapper initialize() {
        return Mappers.getMapper(CustomPageCarToCustomPagingCarResponseMapper.class);
    }

}

