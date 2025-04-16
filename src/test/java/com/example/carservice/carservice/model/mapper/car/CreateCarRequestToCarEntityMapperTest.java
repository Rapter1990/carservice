package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateCarRequestToCarEntityMapperTest {

    private final CreateCarRequestToCarEntityMapper mapper = new CreateCarRequestToCarEntityMapperImpl();

    @Test
    void testMap_NullRequest_ReturnsNull() {
        // When
        CarEntity result = mapper.map((CreateCarRequest) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMap_NullCollection_ReturnsNull() {
        // When
        List<CarEntity> result = mapper.map((Collection<CreateCarRequest>) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMap_EmptyList_ReturnsEmptyList() {
        // When
        List<CarEntity> result = mapper.map(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMap_SingleRequest() {
        // Given
        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .model("Civic")
                .brand("Honda")
                .build();

        // When
        CarEntity result = mapper.map(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getLicensePlate(), result.getLicensePlate());
        assertEquals(request.getModel(), result.getModel());
        assertEquals(request.getBrand(), result.getBrand());
    }

    @Test
    void testMap_ListWithNullElement() {
        // Given
        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("06 XYZ 987")
                .model("Focus")
                .brand("Ford")
                .build();

        List<CreateCarRequest> requests = Arrays.asList(request, null);

        // When
        List<CarEntity> result = mapper.map(requests);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1));
    }


}