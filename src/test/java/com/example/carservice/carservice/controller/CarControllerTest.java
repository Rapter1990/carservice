package com.example.carservice.carservice.controller;

import com.example.carservice.auth.config.TokenConfigurationParameter;
import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.base.AbstractRestControllerTest;
import com.example.carservice.builder.UserIdentityBuilder;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.carservice.model.mapper.car.CarToCarResponseMapper;
import com.example.carservice.carservice.service.CarService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
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
        // Given: Token user ID does not match request's userId
        String tokenUserId = userIdentityBuilder.extractUserIdFromToken(mockUserToken.getAccessToken());

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
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.httpStatus").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").value("You are not authorized to create a car for another user."))
                .andExpect(jsonPath("$.isSuccess").value(false));

        verify(carService, times(1)).assignCarToUser(any(CreateCarRequest.class));
    }


}