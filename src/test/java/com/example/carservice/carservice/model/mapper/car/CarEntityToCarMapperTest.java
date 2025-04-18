package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CarEntityToCarMapperTest {

    private final CarEntityToCarMapper mapper = CarEntityToCarMapper.initialize();

    @Test
    void testMapNullEntity() {
        // Given & When
        Car result = mapper.map((CarEntity) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMapNullEntityCollection() {
        // Given & When
        List<Car> result = mapper.map((Collection<CarEntity>) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMapEmptyEntityList() {
        // Given & When
        List<Car> result = mapper.map(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapListWithNullElements() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .firstName("testuser")
                .build();

        CarEntity car1 = CarEntity.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 ABC 123")
                .model("Corolla")
                .brand("Toyota")
                .status(CarStatus.ACTIVE)
                .user(user)
                .build();

        List<CarEntity> carEntities = Arrays.asList(car1, null);

        // When
        List<Car> result = mapper.map(carEntities);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    void testMapSingleCarEntity() {
        // Given
        String userId = UUID.randomUUID().toString();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("testuser")
                .build();

        CarEntity carEntity = CarEntity.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 ABC 123")
                .model("Corolla")
                .brand("Toyota")
                .status(CarStatus.ACTIVE)
                .user(user)
                .services(Collections.emptyList())
                .build();

        // When
        Car result = mapper.map(carEntity);

        // Then
        assertNotNull(result);
        assertEquals(carEntity.getId(), result.getId());
        assertEquals(carEntity.getLicensePlate(), result.getLicensePlate());
        assertEquals(carEntity.getBrand(), result.getBrand());
        assertEquals(carEntity.getModel(), result.getModel());
        assertEquals(carEntity.getStatus(), result.getStatus());
        assertNotNull(result.getUser());
        assertEquals(userId, result.getUser().getId());
        assertNull(result.getServiceList());

    }

    @Test
    void testMapCarEntityWithServices() {
        // Given
        String userId = UUID.randomUUID().toString();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("serviceuser")
                .build();

        String serviceId = UUID.randomUUID().toString();
        ServiceEntity serviceEntity = ServiceEntity.builder()
                .id(serviceId)
                .description("Oil Change")
                .build();

        CarEntity carEntity = CarEntity.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 XYZ 456")
                .model("Civic")
                .brand("Honda")
                .status(CarStatus.ACTIVE)
                .user(user)
                .services(List.of(serviceEntity))
                .build();

        // When
        Car result = mapper.mapFromEntity(carEntity);

        // Then
        assertNotNull(result);
        assertNotNull(result.getServiceList());
        assertEquals(1, result.getServiceList().size());

        ServiceDto service = result.getServiceList().get(0);
        assertNotNull(service);
        assertEquals(serviceEntity.getId(), service.getId());
        assertEquals(serviceEntity.getDescription(), service.getDescription());

    }

}