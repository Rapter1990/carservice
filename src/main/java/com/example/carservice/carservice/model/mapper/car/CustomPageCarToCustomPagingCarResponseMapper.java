package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CustomPageCarToCustomPagingCarResponseMapper {

    CarToCarResponseMapper carToCarResponseMapper = Mappers.getMapper(CarToCarResponseMapper.class);

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

    default List<CarResponse> toCarResponseList(List<Car> cars) {
        if (cars == null) {
            return null;
        }

        return cars.stream()
                .map(carToCarResponseMapper::mapToResponse)
                .collect(Collectors.toList());
    }

    static CustomPageCarToCustomPagingCarResponseMapper initialize() {
        return Mappers.getMapper(CustomPageCarToCustomPagingCarResponseMapper.class);
    }

}

