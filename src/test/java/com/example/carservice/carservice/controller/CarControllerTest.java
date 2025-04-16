package com.example.carservice.carservice.controller;

import com.example.carservice.auth.model.User;
import com.example.carservice.base.AbstractRestControllerTest;
import com.example.carservice.builder.UserIdentityBuilder;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.carservice.model.mapper.car.CarToCarResponseMapper;
import com.example.carservice.carservice.service.CarService;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.CustomPaging;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    CarService carService;

    @MockitoBean
    UserIdentityBuilder userIdentityBuilder;

    private final CarToCarResponseMapper carToCarResponseMapper = CarToCarResponseMapper.initialize();

    @Test
    void testCreateCarWithUserToken() throws Exception {

        // Given
        final String userId = userIdentityBuilder.extractUserIdFromToken(mockUserToken.getAccessToken());

        final CreateCarRequest createRequest = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .model("Civic")
                .brand("Honda")
                .userId(userId)
                .build();

        final Car sampleCar = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate(createRequest.getLicensePlate())
                .model(createRequest.getModel())
                .brand(createRequest.getBrand())
                .userId(createRequest.getUserId())
                .status(CarStatus.ACTIVE)
                .user(User.builder()
                        .id(createRequest.getUserId())
                        .firstName("User")
                        .lastName("Example")
                        .build())
                .serviceList(Collections.emptyList())
                .build();

        final CarResponse expected = carToCarResponseMapper.mapToResponse(sampleCar);

        // When
        when(carService.assignCarToUser(any(CreateCarRequest.class))).thenReturn(sampleCar);

        // Then
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.id").value(expected.getId()))
                .andExpect(jsonPath("$.response.licensePlate").value(expected.getLicensePlate()))
                .andExpect(jsonPath("$.response.model").value(expected.getModel()))
                .andExpect(jsonPath("$.response.brand").value(expected.getBrand()))
                .andExpect(jsonPath("$.response.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.response.status").value(expected.getStatus().toString()));

        // Verify
        verify(carService, times(1)).assignCarToUser(any(CreateCarRequest.class));

    }

    @Test
    void testCreateCarWithAdminToken() throws Exception {

        // Given
        final String userId = userIdentityBuilder.extractUserIdFromToken(mockUserToken.getAccessToken());

        final CreateCarRequest createRequest = CreateCarRequest.builder()
                .licensePlate("06 XYZ 789")
                .model("Model 3")
                .brand("Tesla")
                .userId(userId)
                .build();

        final Car sampleCar = Car.builder()
                .id(UUID.randomUUID().toString())
                .licensePlate(createRequest.getLicensePlate())
                .model(createRequest.getModel())
                .brand(createRequest.getBrand())
                .userId(createRequest.getUserId())
                .status(CarStatus.ACTIVE)
                .user(User.builder()
                        .id(createRequest.getUserId())
                        .firstName("Admin")
                        .lastName("User")
                        .build())
                .serviceList(Collections.emptyList())
                .build();

        final CarResponse expected = carToCarResponseMapper.mapToResponse(sampleCar);

        // When
        when(carService.assignCarToUser(any(CreateCarRequest.class))).thenReturn(sampleCar);

        // Then
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.id").value(expected.getId()))
                .andExpect(jsonPath("$.response.licensePlate").value(expected.getLicensePlate()))
                .andExpect(jsonPath("$.response.model").value(expected.getModel()))
                .andExpect(jsonPath("$.response.brand").value(expected.getBrand()))
                .andExpect(jsonPath("$.response.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.response.status").value(expected.getStatus().toString()));

        // Verify
        verify(carService, times(1)).assignCarToUser(any(CreateCarRequest.class));

    }

    @Test
    void testAssignCarToUser_AccessDeniedException() throws Exception {

        final CreateCarRequest createRequest = CreateCarRequest.builder()
                .licensePlate("10 X 1234")
                .model("Fiesta")
                .brand("Ford")
                .userId("unauthorized-user-id")
                .build();

        when(carService.assignCarToUser(any(CreateCarRequest.class)))
                .thenThrow(new AccessDeniedException("You are not authorized to create a car for another user."));

        // When & Then
        mockMvc.perform(post("/api/v1/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.httpStatus").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("You are not authorized to create a car for another user."))
                .andExpect(jsonPath("$.isSuccess").value(false));

        verify(carService, times(1)).assignCarToUser(any(CreateCarRequest.class));

    }

    @Test
    void testGetCarById_AsOwner_ReturnsCar() throws Exception {

        // Given
        String userId = userIdentityBuilder.extractUserIdFromToken(mockUserToken.getAccessToken());

        String carId = UUID.randomUUID().toString();

        Car car = Car.builder()
                .id(carId)
                .licensePlate("34 ABC 123")
                .model("Civic")
                .brand("Honda")
                .status(CarStatus.ACTIVE)
                .userId(userId)
                .user(User.builder().id(userId).firstName("User").lastName("Example").build())
                .serviceList(Collections.emptyList())
                .build();

        CarResponse expected = carToCarResponseMapper.mapToResponse(car);

        // When
        when(carService.getCarById(carId)).thenReturn(car);

        // Then
        mockMvc.perform(get("/api/v1/cars/" + carId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.id").value(expected.getId()))
                .andExpect(jsonPath("$.response.licensePlate").value(expected.getLicensePlate()))
                .andExpect(jsonPath("$.response.model").value(expected.getModel()))
                .andExpect(jsonPath("$.response.brand").value(expected.getBrand()))
                .andExpect(jsonPath("$.response.status").value(expected.getStatus().toString()))
                .andExpect(jsonPath("$.response.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.response.user.id").value(expected.getUser().getId()))
                .andExpect(jsonPath("$.response.user.firstName").value(expected.getUser().getFirstName()))
                .andExpect(jsonPath("$.response.user.lastName").value(expected.getUser().getLastName()))
                .andExpect(jsonPath("$.response.serviceList").isArray())
                .andExpect(jsonPath("$.response.serviceList.length()").value(0));

        // Verify
        verify(carService, times(1)).getCarById(carId);

    }

    @Test
    void testGetCarById_AsAdmin_ReturnsCar() throws Exception {

        // Given
        String carId = UUID.randomUUID().toString();

        Car car = Car.builder()
                .id(carId)
                .licensePlate("06 XYZ 789")
                .model("Model S")
                .brand("Tesla")
                .status(CarStatus.ACTIVE)
                .userId("some-other-user-id")
                .user(User.builder().id("some-other-user-id").firstName("Other").lastName("User").build())
                .serviceList(Collections.emptyList())
                .build();

        CarResponse expected = carToCarResponseMapper.mapToResponse(car);

        // When
        when(carService.getCarById(carId)).thenReturn(car);

        // Then
        mockMvc.perform(get("/api/v1/cars/" + carId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.id").value(expected.getId()))
                .andExpect(jsonPath("$.response.licensePlate").value(expected.getLicensePlate()))
                .andExpect(jsonPath("$.response.model").value(expected.getModel()))
                .andExpect(jsonPath("$.response.brand").value(expected.getBrand()))
                .andExpect(jsonPath("$.response.status").value(expected.getStatus().toString()))
                .andExpect(jsonPath("$.response.userId").value(expected.getUserId()))
                .andExpect(jsonPath("$.response.user.id").value(expected.getUser().getId()))
                .andExpect(jsonPath("$.response.user.firstName").value(expected.getUser().getFirstName()))
                .andExpect(jsonPath("$.response.user.lastName").value(expected.getUser().getLastName()))
                .andExpect(jsonPath("$.response.serviceList").isArray())
                .andExpect(jsonPath("$.response.serviceList.length()").value(0));

        // Verify
        verify(carService, times(1)).getCarById(carId);

    }

    @Test
    void givenCarId_whenGetCarWithoutToken_thenReturnUnauthorized() throws Exception {

        // Given
        String carId = UUID.randomUUID().toString();

        // When & Then
        mockMvc.perform(get("/api/v1/cars/" + carId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        // Verify
        verify(carService, never()).getCarById(any());

    }

    @Test
    void testGetAllCarsByUser_ReturnsPagedCars() throws Exception {

        // Given
        String userId = UUID.randomUUID().toString();

        CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        List<Car> carList = List.of(
                Car.builder()
                        .id(UUID.randomUUID().toString())
                        .licensePlate("34 USER 001")
                        .brand("Toyota")
                        .model("Corolla")
                        .status(CarStatus.ACTIVE)
                        .userId(userId)
                        .serviceList(Collections.emptyList())
                        .build(),
                Car.builder()
                        .id(UUID.randomUUID().toString())
                        .licensePlate("34 USER 002")
                        .brand("Renault")
                        .model("Clio")
                        .status(CarStatus.ACTIVE)
                        .userId(userId)
                        .serviceList(Collections.emptyList())
                        .build()
        );

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(carList, pageRequest, carList.size());
        CustomPage<Car> customPage = CustomPage.of(carList, carPage);

        // When
        when(carService.getAllCarsByUser(eq(userId), any(CustomPagingRequest.class))).thenReturn(customPage);

        // Then
        mockMvc.perform(get("/api/v1/cars/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagingRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockUserToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.content.length()").value(carList.size()))
                .andExpect(jsonPath("$.response.content[0].licensePlate").value("34 USER 001"))
                .andExpect(jsonPath("$.response.content[0].brand").value("Toyota"))
                .andExpect(jsonPath("$.response.content[0].model").value("Corolla"))
                .andExpect(jsonPath("$.response.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.response.content[1].licensePlate").value("34 USER 002"))
                .andExpect(jsonPath("$.response.content[1].brand").value("Renault"))
                .andExpect(jsonPath("$.response.content[1].model").value("Clio"))
                .andExpect(jsonPath("$.response.content[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$.response.totalElementCount").value(carList.size()))
                .andExpect(jsonPath("$.response.pageNumber").value(customPage.getPageNumber()))
                .andExpect(jsonPath("$.response.pageSize").value(customPage.getPageSize()));

        // Verify
        verify(carService).getAllCarsByUser(eq(userId), any(CustomPagingRequest.class));

    }


    @Test
    void testGetAllCars_AsAdmin_ReturnsPagedCars() throws Exception {

        // Given
        CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        List<Car> carList = List.of(
                Car.builder()
                        .id(UUID.randomUUID().toString())
                        .licensePlate("06 ADMIN 001")
                        .brand("Tesla")
                        .model("Model S")
                        .status(CarStatus.ACTIVE)
                        .userId(UUID.randomUUID().toString())
                        .serviceList(Collections.emptyList())
                        .build(),
                Car.builder()
                        .id(UUID.randomUUID().toString())
                        .licensePlate("06 ADMIN 002")
                        .brand("BMW")
                        .model("i8")
                        .status(CarStatus.ACTIVE)
                        .userId(UUID.randomUUID().toString())
                        .serviceList(Collections.emptyList())
                        .build()
        );

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Car> carPage = new PageImpl<>(carList, pageRequest, carList.size());

        CustomPage<Car> customPage = CustomPage.of(carList, carPage);

        // When
        when(carService.getAllCars(any(CustomPagingRequest.class))).thenReturn(customPage);

        // Then
        mockMvc.perform(get("/api/v1/cars/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagingRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + mockAdminToken.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.response.content").isArray())
                .andExpect(jsonPath("$.response.content.length()").value(carList.size()))
                .andExpect(jsonPath("$.response.content[0].licensePlate").value("06 ADMIN 001"))
                .andExpect(jsonPath("$.response.content[0].brand").value("Tesla"))
                .andExpect(jsonPath("$.response.content[0].model").value("Model S"))
                .andExpect(jsonPath("$.response.content[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.response.content[1].licensePlate").value("06 ADMIN 002"))
                .andExpect(jsonPath("$.response.content[1].brand").value("BMW"))
                .andExpect(jsonPath("$.response.content[1].model").value("i8"))
                .andExpect(jsonPath("$.response.content[1].status").value("ACTIVE"))
                .andExpect(jsonPath("$.response.totalElementCount").value(carList.size()))
                .andExpect(jsonPath("$.response.pageNumber").value(customPage.getPageNumber()))
                .andExpect(jsonPath("$.response.pageSize").value(customPage.getPageSize()));

        // Verify
        verify(carService).getAllCars(any(CustomPagingRequest.class));

    }

}