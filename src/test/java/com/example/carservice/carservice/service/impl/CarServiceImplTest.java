package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.CarNotFoundException;
import com.example.carservice.carservice.exception.LicensePlateAlreadyExistsException;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.mapper.car.CarEntityToCarMapper;
import com.example.carservice.carservice.model.mapper.car.CreateCarRequestToCarEntityMapper;
import com.example.carservice.carservice.model.mapper.car.ListCarEntityToListCarMapper;
import com.example.carservice.carservice.repository.CarRepository;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.CustomPaging;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
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

    private final ListCarEntityToListCarMapper listCarEntityToListCarMapper = ListCarEntityToListCarMapper.initialize();


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
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(mockUserEntity));
        when(carRepository.existsByLicensePlate(request.getLicensePlate())).thenReturn(false);
        when(carRepository.save(any(CarEntity.class))).thenReturn(savedCarEntity);

        // When
        Car result = carService.assignCarToUser(request);

        // Then
        assertNotNull(result);
        assertEquals(expected.getLicensePlate(), result.getLicensePlate());
        assertEquals(expected.getBrand(), result.getBrand());

        // Verify
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
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

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

        // When
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> carService.assignCarToUser(request));

        // Verify
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

        // When
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.existsByLicensePlate("34 ABC 123")).thenReturn(true);

        // Then
        assertThrows(LicensePlateAlreadyExistsException.class, () -> carService.assignCarToUser(request));

        // Verify
        verify(userIdentity).getUserId();
        verify(userRepository).findById(userId);
        verify(carRepository).existsByLicensePlate("34 ABC 123");
        verify(carRepository, never()).save(any(CarEntity.class));

    }

    @Test
    void givenCarExistsAndUserIsOwner_whenGetCarById_thenReturnsCar() {

        // Given
        String userId = UUID.randomUUID().toString();
        String carId = UUID.randomUUID().toString();

        CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 XYZ 456")
                .brand("Honda")
                .model("Civic")
                .user(UserEntity.builder().id(userId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        Car result = carService.getCarById(carId);

        assertNotNull(result);
        assertEquals(carId, result.getId());

        // Verify
        verify(carRepository).findById(carId);
        verify(userIdentity).getUserId();
        verify(userIdentity).getUserType();

    }

    @Test
    void givenCarExistsAndUserIsAdmin_whenGetCarById_thenReturnsCar() {

        // Given
        String carId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("06 ABC 123")
                .brand("Ford")
                .model("Focus")
                .user(UserEntity.builder().id(userId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserType()).thenReturn(UserType.ADMIN);

        // Then
        Car result = carService.getCarById(carId);

        assertNotNull(result);
        assertEquals(carId, result.getId());

        // Verify
        verify(carRepository).findById(carId);
        verify(userIdentity).getUserType();
        verify(userIdentity, times(1)).getUserId();

    }

    @Test
    void givenUserIsNotOwnerAndNotAdmin_whenGetCarById_thenThrowsAccessDeniedException() {

        // Given
        String ownerId = UUID.randomUUID().toString();
        String otherUserId = UUID.randomUUID().toString();
        String carId = UUID.randomUUID().toString();

        CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("07 DENY 789")
                .brand("BMW")
                .model("320i")
                .user(UserEntity.builder().id(ownerId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(otherUserId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        assertThrows(AccessDeniedException.class, () -> carService.getCarById(carId));

        // Verify
        verify(carRepository).findById(carId);
        verify(userIdentity).getUserType();
        verify(userIdentity).getUserId();

    }

    @Test
    void givenCarDoesNotExist_whenGetCarById_thenThrowsCarNotFoundException() {

        // Given
        String carId = UUID.randomUUID().toString();
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(CarNotFoundException.class, () -> carService.getCarById(carId));

        // Verify
        verify(carRepository).findById(carId);
        verifyNoInteractions(userIdentity);

    }

    @Test
    void givenPagingRequest_whenGetAllCars_thenReturnPagedCars() {

        // Given
        String carId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 TEST 001")
                .brand("Audi")
                .model("A4")
                .user(UserEntity.builder().id(userId).build())
                .build();

        Page<CarEntity> page = new PageImpl<>(List.of(carEntity));

        when(carRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        CustomPage<Car> result = carService.getAllCars(pagingRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Audi", result.getContent().get(0).getBrand());

        // Verify
        verify(carRepository).findAll(any(Pageable.class));

    }

    @Test
    void givenUserIdAndPagingRequest_whenGetAllCarsByUser_thenReturnPagedCars() {

        // Given
        String carId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();

        CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("35 TEST 002")
                .brand("Renault")
                .model("Clio")
                .user(UserEntity.builder().id(userId).build())
                .build();

        Page<CarEntity> page = new PageImpl<>(List.of(carEntity));

        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(carRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        // When
        CustomPage<Car> result = carService.getAllCarsByUser(userId, pagingRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Clio", result.getContent().get(0).getModel());

        // Verify
        verify(carRepository).findByUserId(eq(userId), any(Pageable.class));

    }

}