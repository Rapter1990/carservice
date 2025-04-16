package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.User;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.Service;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.carservice.model.enums.CarStatus;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CarToCarResponseMapperTest {

    private final CarToCarResponseMapper mapper = CarToCarResponseMapper.initialize();

    @Test
    void testMapNullCar() {
        // When
        CarResponse result = mapper.map((Car) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMapNullCollection() {
        // When
        List<CarResponse> result = mapper.map((Collection<Car>) null);

        // Then
        assertNull(result);
    }

    @Test
    void testMapEmptyCarList() {
        // When
        List<CarResponse> result = mapper.map(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapListWithNullElement() {
        // Given
        Car car = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 XYZ 456")
                .model("Focus")
                .brand("Ford")
                .status(CarStatus.ACTIVE)
                .serviceList(Collections.emptyList())
                .build();

        List<Car> cars = Arrays.asList(car, null);

        // When
        List<CarResponse> result = mapper.map(cars);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNull(result.get(1));
    }

    @Test
    void testMapSingleCar() {
        // Given
        String userId = UUID.randomUUID().toString();
        User user = User.builder()
                .id(userId)
                .firstName("carowner")
                .build();

        Car car = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 ABC 123")
                .brand("Toyota")
                .model("Corolla")
                .status(CarStatus.ACTIVE)
                .userId(userId)
                .user(user)
                .serviceList(Collections.emptyList())
                .build();

        // When
        CarResponse result = mapper.map(car);

        // Then
        assertNotNull(result);
        assertEquals(car.getId(), result.getId());
        assertEquals(car.getBrand(), result.getBrand());
        assertEquals(car.getModel(), result.getModel());
        assertEquals(car.getStatus(), result.getStatus());
        assertEquals(car.getUserId(), result.getUserId());
        assertNotNull(result.getUser());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(user.getFirstName(), result.getUser().getFirstName());
        assertNotNull(result.getServiceList());
        assertTrue(result.getServiceList().isEmpty());
    }

    @Test
    void testMapCarWithServices() {
        // Given
        Service service = Service.builder()
                .id(UUID.randomUUID().toString())
                .description("Tire Change")
                .build();

        Car car = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("06 DEF 789")
                .model("Civic")
                .brand("Honda")
                .status(CarStatus.ACTIVE)
                .serviceList(List.of(service))
                .build();

        // When
        CarResponse result = mapper.map(car);

        // Then
        assertNotNull(result);
        assertNotNull(result.getServiceList());
        assertEquals(1, result.getServiceList().size());

        ServiceResponse serviceResponse = result.getServiceList().get(0);
        assertNotNull(serviceResponse);
        assertEquals(service.getId(), serviceResponse.getId());
        assertEquals(service.getDescription(), serviceResponse.getDescription());

    }

    @Test
    void testMapToResponse_WithAllFields() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("johndoe")
                .build();

        Service service = Service.builder()
                .id(UUID.randomUUID().toString())
                .description("Oil Change")
                .build();

        Car car = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("34 ABC 123")
                .model("Corolla")
                .brand("Toyota")
                .userId(user.getId())
                .status(CarStatus.ACTIVE)
                .user(user)
                .serviceList(List.of(service))
                .build();

        // When
        CarResponse result = mapper.mapToResponse(car);

        // Then
        assertNotNull(result);
        assertEquals(car.getId(), result.getId());
        assertEquals(car.getLicensePlate(), result.getLicensePlate());
        assertEquals(car.getModel(), result.getModel());
        assertEquals(car.getBrand(), result.getBrand());
        assertEquals(car.getUserId(), result.getUserId());
        assertEquals(car.getStatus(), result.getStatus());

        assertNotNull(result.getUser());
        assertEquals(user.getId(), result.getUser().getId());
        assertEquals(user.getFirstName(), result.getUser().getFirstName());

        assertNotNull(result.getServiceList());
        assertEquals(1, result.getServiceList().size());
        assertEquals(service.getId(), result.getServiceList().get(0).getId());
        assertEquals(service.getDescription(), result.getServiceList().get(0).getDescription());
    }

    @Test
    void testMapToResponse_NullServiceList() {
        // Given
        User user = User.builder()
                .id("user-123")
                .firstName("serviceuser")
                .build();

        Car car = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate("35 ZZZ 999")
                .model("Astra")
                .brand("Opel")
                .userId(user.getId())
                .status(CarStatus.ACTIVE)
                .user(user)
                .serviceList(null) // service list is null
                .build();

        // When
        CarResponse result = mapper.mapToResponse(car);

        // Then
        assertNotNull(result);
        assertNotNull(result.getServiceList());
        assertTrue(result.getServiceList().isEmpty());
    }

}