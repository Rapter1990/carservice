package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateServiceRequestToServiceEntityMapperTest {

    private final CreateServiceRequestToServiceEntityMapper mapper = new CreateServiceRequestToServiceEntityMapperImpl();

    @Test
    void testMap_NullRequest_ReturnsNull() {

        // When
        ServiceEntity result = mapper.map((CreateServiceRequest) null);

        // Then
        assertNull(result);

    }

    @Test
    void testMap_NullCollection_ReturnsNull() {

        // When
        List<ServiceEntity> result = mapper.map((Collection<CreateServiceRequest>) null);

        // Then
        assertNull(result);

    }

    @Test
    void testMap_EmptyList_ReturnsEmptyList() {
        // When
        List<ServiceEntity> result = mapper.map(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

    }

    @Test
    void testMap_SingleRequest() {
        // Given
        CreateServiceRequest request = CreateServiceRequest.builder()
                .title("Brake Inspection")
                .description("Check brake pads and discs")
                .status(ServiceStatus.CREATED)
                .build();

        // When
        ServiceEntity result = mapper.map(request);

        // Then
        assertNotNull(result);
        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getStatus(), result.getStatus());

    }

    @Test
    void testMap_ListWithNullElement() {
        // Given
        CreateServiceRequest request = CreateServiceRequest.builder()
                .title("Oil Change")
                .description("Routine engine oil replacement")
                .status(ServiceStatus.IN_PROGRESS)
                .build();

        List<CreateServiceRequest> requests = Arrays.asList(request, null);

        // When
        List<ServiceEntity> result = mapper.map(requests);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertEquals(request.getTitle(), result.get(0).getTitle());
        assertEquals(request.getDescription(), result.get(0).getDescription());
        assertEquals(request.getStatus(), result.get(0).getStatus());
        assertNull(result.get(1));

    }

}