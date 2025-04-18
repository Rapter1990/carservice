package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceEntityToServiceDtoMapperTest {

    private final ServiceEntityToServiceDtoMapper mapper = new ServiceEntityToServiceDtoMapperImpl();

    @Test
    void testMapCollection_whenSourcesIsNull_thenReturnNull() {

        // When
        List<ServiceDto> result = mapper.map((Collection<ServiceEntity>) null);

        // Then
        assertNull(result);

    }

    @Test
    void testMapSingle_whenSourceIsNull_thenReturnNull() {

        // When
        ServiceDto result = mapper.map((ServiceEntity) null);

        // Then
        assertNull(result);

    }

    @Test
    void testMapCollection_whenSourcesIsNotNull_thenReturnMappedList() {

        // Given
        ServiceEntity entity1 = ServiceEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Service One")
                .description("Description One")
                .status(ServiceStatus.CREATED)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now())
                .build();

        ServiceEntity entity2 = ServiceEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Service Two")
                .description("Description Two")
                .status(ServiceStatus.CREATED)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        List<ServiceEntity> sourceList = List.of(entity1, entity2);

        // When
        List<ServiceDto> result = mapper.map(sourceList);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        ServiceDto dto1 = result.get(0);
        assertEquals(entity1.getId(), dto1.getId());
        assertEquals(entity1.getTitle(), dto1.getTitle());
        assertEquals(entity1.getDescription(), dto1.getDescription());
        assertEquals(entity1.getStatus(), dto1.getStatus());
        assertEquals(entity1.getCreatedAt(), dto1.getCreatedAt());
        assertEquals(entity1.getUpdatedAt(), dto1.getUpdatedAt());

        ServiceDto dto2 = result.get(1);
        assertEquals(entity2.getId(), dto2.getId());
        assertEquals(entity2.getTitle(), dto2.getTitle());
        assertEquals(entity2.getDescription(), dto2.getDescription());
        assertEquals(entity2.getStatus(), dto2.getStatus());
        assertEquals(entity2.getCreatedAt(), dto2.getCreatedAt());
        assertEquals(entity2.getUpdatedAt(), dto2.getUpdatedAt());

    }


}