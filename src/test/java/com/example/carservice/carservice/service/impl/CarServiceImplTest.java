package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.LicensePlateAlreadyExistsException;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.mapper.car.CarEntityToCarMapper;
import com.example.carservice.carservice.model.mapper.car.CreateCarRequestToCarEntityMapper;
import com.example.carservice.carservice.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private CarServiceImpl carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserIdentity userIdentity;

    private final CreateCarRequestToCarEntityMapper createCarRequestToCarEntityMapper = CreateCarRequestToCarEntityMapper.initialize();

    private final CarEntityToCarMapper carEntityToCarMapper = CarEntityToCarMapper.initialize();


    @Test
    void givenValidCreateCarRequest_WhenAssignCarToUser_ThenReturnSuccess() {

        // Given
        String userId = UUID.randomUUID().toString();

        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(userId)
                .build();

        UserEntity mockUserEntity = UserEntity.builder()
                .id(userId)
                .firstName("testuser")
                .build();

        CarEntity unsavedCarEntity = createCarRequestToCarEntityMapper.mapForSaving(request, mockUserEntity);
        CarEntity savedCarEntity = CarEntity.builder()
                .id(unsavedCarEntity.getId())
                .licensePlate(unsavedCarEntity.getLicensePlate())
                .model(unsavedCarEntity.getModel())
                .brand(unsavedCarEntity.getBrand())
                .user(unsavedCarEntity.getUser())
                .build();

        Car expected = carEntityToCarMapper.mapFromEntity(savedCarEntity);


        when(userIdentity.getUserId()).thenReturn(request.getUserId());
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(mockUserEntity));
        when(carRepository.existsByLicensePlate(request.getLicensePlate())).thenReturn(false);
        when(carRepository.save(any(CarEntity.class))).thenReturn(savedCarEntity);

        // When
        Car result = carService.assignCarToUser(request);

        // Then
        assertNotNull(result);
        assertEquals(expected.getLicensePlate(), result.getLicensePlate());
        assertEquals(expected.getBrand(), result.getBrand());
        verify(userRepository).findById(request.getUserId());
        verify(carRepository).existsByLicensePlate("34 ABC 123");
        verify(carRepository).save(any(CarEntity.class));

    }

    @Test
    void givenValidCreateCarRequest_WhenAssignCarToUser_UserMismatch_ThrowsAccessDenied() {

        // Given
        String actualUserId = UUID.randomUUID().toString();
        String requestUserId = UUID.randomUUID().toString();

        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(requestUserId)
                .build();

        // When
        when(userIdentity.getUserId()).thenReturn(actualUserId);

        // Then
        assertThrows(AccessDeniedException.class, () -> carService.assignCarToUser(request));

        // Verify
        verify(userIdentity).getUserId();
        verifyNoInteractions(userRepository, carRepository);

    }

    @Test
    void givenValidCreateCarRequest_WhenAssignCarToUser_UserNotFound_ThrowsException() {

        // Given
        String userId = UUID.randomUUID().toString();

        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(userId)
                .build();

        when(userIdentity.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> carService.assignCarToUser(request));

        verify(userIdentity).getUserId();
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(carRepository);
    }

    @Test
    void givenValidCreateCarRequest_WhenAssignCarToUser_LicensePlateAlreadyExists_ThrowsException() {
        // Given
        String userId = UUID.randomUUID().toString();
        CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 ABC 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(userId)
                .build();

        UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("testuser")
                .build();

        when(userIdentity.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.existsByLicensePlate("34 ABC 123")).thenReturn(true);

        // When & Then
        assertThrows(LicensePlateAlreadyExistsException.class, () -> carService.assignCarToUser(request));

        verify(userIdentity).getUserId();
        verify(userRepository).findById(userId);
        verify(carRepository).existsByLicensePlate("34 ABC 123");
        verify(carRepository, never()).save(any(CarEntity.class));

    }


}