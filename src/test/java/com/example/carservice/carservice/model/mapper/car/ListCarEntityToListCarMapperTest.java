package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListCarEntityToListCarMapperTest {

    private final ListCarEntityToListCarMapper mapper = ListCarEntityToListCarMapper.initialize();

    @Test
    void testToCarList_whenCarEntitiesIsNull_thenReturnNull() {
        // Given
        List<CarEntity> carEntities = null;

        // When
        List<Car> result = mapper.toCarList(carEntities);

        // Then
        assertNull(result);
    }

}