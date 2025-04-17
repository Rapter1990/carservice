package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomPageCarToCustomPagingCarResponseMapperTest {

    private final CustomPageCarToCustomPagingCarResponseMapper mapper =
            CustomPageCarToCustomPagingCarResponseMapper.initialize();

    @Test
    void testToPagingResponse_whenCarPageIsNull_thenReturnNull() {
        // Given
        CustomPage<Car> carPage = null;

        // When
        CustomPagingResponse<CarResponse> result = mapper.toPagingResponse(carPage);

        // Then
        assertNull(result);
    }

    @Test
    void testToCarResponseList_whenCarsIsNull_thenReturnNull() {
        // Given
        List<Car> cars = null;

        // When
        List<CarResponse> result = mapper.toCarResponseList(cars);

        // Then
        assertNull(result);
    }

}