package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.CarNotFoundException;
import com.example.carservice.carservice.exception.CarStatusNotValidException;
import com.example.carservice.carservice.exception.LicensePlateAlreadyExistsException;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
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
        final String userId = UUID.randomUUID().toString();

        final CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 AB 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(userId)
                .build();

        final UserEntity mockUserEntity = UserEntity.builder()
                .id(userId)
                .firstName("testuser")
                .build();

        final CarEntity unsavedCarEntity = createCarRequestToCarEntityMapper.mapForSaving(request, mockUserEntity);
        final CarEntity savedCarEntity = CarEntity.builder()
                .id(unsavedCarEntity.getId())
                .licensePlate(unsavedCarEntity.getLicensePlate())
                .model(unsavedCarEntity.getModel())
                .brand(unsavedCarEntity.getBrand())
                .user(unsavedCarEntity.getUser())
                .build();

        final Car expected = carEntityToCarMapper.mapFromEntity(savedCarEntity);

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
        verify(carRepository).existsByLicensePlate("34 AB 123");
        verify(carRepository).save(any(CarEntity.class));

    }

    @Test
    void givenValidCreateCarRequest_WhenAssignCarToUser_UserMismatch_ThrowsAccessDenied() {

        // Given
        final String actualUserId = UUID.randomUUID().toString();
        final String requestUserId = UUID.randomUUID().toString();

        final CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 AB 123")
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
        final String userId = UUID.randomUUID().toString();

        final CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 AB 123")
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
        final String userId = UUID.randomUUID().toString();
        final CreateCarRequest request = CreateCarRequest.builder()
                .licensePlate("34 AB 123")
                .brand("Toyota")
                .model("Corolla")
                .userId(userId)
                .build();

        final UserEntity user = UserEntity.builder()
                .id(userId)
                .firstName("testuser")
                .build();

        // When
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.existsByLicensePlate("34 AB 123")).thenReturn(true);

        // Then
        assertThrows(LicensePlateAlreadyExistsException.class, () -> carService.assignCarToUser(request));

        // Verify
        verify(userIdentity).getUserId();
        verify(userRepository).findById(userId);
        verify(carRepository).existsByLicensePlate("34 AB 123");
        verify(carRepository, never()).save(any(CarEntity.class));

    }

    @Test
    void givenCarExistsAndUserIsOwner_whenGetCarById_thenReturnsCar() {

        // Given
        final String userId = UUID.randomUUID().toString();
        final String carId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 XY 456")
                .brand("Honda")
                .model("Civic")
                .user(UserEntity.builder().id(userId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        final Car result = carService.getCarById(carId);

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
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("06 ABC 23")
                .brand("Ford")
                .model("Focus")
                .user(UserEntity.builder().id(userId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserType()).thenReturn(UserType.ADMIN);

        // Then
        final Car result = carService.getCarById(carId);

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
        final String ownerId = UUID.randomUUID().toString();
        final String otherUserId = UUID.randomUUID().toString();
        final String carId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("33 TT 002")
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
        final String carId = UUID.randomUUID().toString();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // Then
        assertThrows(CarNotFoundException.class, () -> carService.getCarById(carId));

        // Verify
        verify(carRepository).findById(carId);
        verifyNoInteractions(userIdentity);

    }

    @Test
    void givenPagingRequest_whenGetAllCars_thenReturnPagedCars() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("33 TT 002")
                .brand("Audi")
                .model("A4")
                .user(UserEntity.builder().id(userId).build())
                .build();

        final Page<CarEntity> page = new PageImpl<>(List.of(carEntity));

        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());

        final CustomPage<Car> expected = CustomPage.of(cars, page);

        when(carRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        final CustomPage<Car> result = carService.getAllCars(pagingRequest);

        // Then
        assertNotNull(result);
        assertEquals(expected.getContent().size(), result.getContent().size());
        assertEquals(expected.getContent().get(0).getBrand(), result.getContent().get(0).getBrand());

        // Verify
        verify(carRepository).findAll(any(Pageable.class));

    }

    @Test
    void givenUserIdAndPagingRequest_whenGetAllActiveCarsByUser_thenReturnPagedCars() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("33 TT 002")
                .brand("Renault")
                .model("Clio")
                .status(CarStatus.ACTIVE)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final Page<CarEntity> page = new PageImpl<>(List.of(carEntity));
        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());
        final CustomPage<Car> expected = CustomPage.of(cars, page);

        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(carRepository.findByUserIdAndStatus(eq(userId), eq(CarStatus.ACTIVE), any(Pageable.class))).thenReturn(page);

        // When
        final CustomPage<Car> result = carService.getAllCarsByUser(userId, pagingRequest);

        // Then
        assertNotNull(result);
        assertEquals(expected.getContent().size(), result.getContent().size());
        assertEquals(expected.getContent().get(0).getModel(), result.getContent().get(0).getModel());

        // Verify
        verify(carRepository).findByUserIdAndStatus(eq(userId), eq(CarStatus.ACTIVE), any(Pageable.class));
    }

    @Test
    void givenActiveCarsExist_whenGetAllCarsByStatus_thenReturnPagedActiveCars() {

        // Given
        final String userId = UUID.randomUUID().toString();
        final String carId = UUID.randomUUID().toString();

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder().pageNumber(1).pageSize(10).build())
                .build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("33 TT 002")
                .brand("Tesla")
                .model("Model 3")
                .status(CarStatus.ACTIVE)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final Page<CarEntity> page = new PageImpl<>(List.of(carEntity));

        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());

        final CustomPage<Car> expected = CustomPage.of(cars, page);

        when(carRepository.findByStatus(eq(CarStatus.ACTIVE), any(Pageable.class))).thenReturn(page);

        // When
        final CustomPage<Car> result = carService.getAllCarsByStatus(pagingRequest);

        // Then
        assertNotNull(result);
        assertEquals(expected.getContent().size(), result.getContent().size());
        assertEquals(expected.getContent().get(0).getStatus(), result.getContent().get(0).getStatus());

        // Verify
        verify(carRepository).findByStatus(eq(CarStatus.ACTIVE), any(Pageable.class));

    }

    @Test
    void givenValidUpdateCarRequest_whenUserIsOwnerOrAdmin_thenUpdateCarSuccessfully() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final UpdateCarRequest updateRequest = UpdateCarRequest.builder()
                .licensePlate("34 UP 123")
                .brand("Peugeot")
                .model("308")
                .status(CarStatus.DELETED)
                .userId(userId)
                .build();

        final UserEntity userEntity = UserEntity.builder().id(userId).build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 OL 999")
                .brand("Fiat")
                .model("Egea")
                .status(CarStatus.ACTIVE)
                .user(userEntity)
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(carRepository.existsByLicensePlate(updateRequest.getLicensePlate())).thenReturn(false);
        when(carRepository.save(any(CarEntity.class))).thenReturn(carEntity);

        // Then
        final Car result = carService.updateCar(carId, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getLicensePlate(), result.getLicensePlate());
        assertEquals(updateRequest.getBrand(), result.getBrand());
        assertEquals(updateRequest.getModel(), result.getModel());
        assertEquals(updateRequest.getStatus(), result.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(carRepository).existsByLicensePlate(updateRequest.getLicensePlate());
        verify(carRepository).save(any(CarEntity.class));

    }

    @Test
    void givenUpdateCarRequest_whenLicensePlateAlreadyExists_thenThrowsException() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final UpdateCarRequest updateRequest = UpdateCarRequest.builder()
                .licensePlate("34 EX 001")
                .brand("VW")
                .model("Golf")
                .status(CarStatus.ACTIVE)
                .userId(userId)
                .build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 OL 000")
                .user(UserEntity.builder().id(userId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(carRepository.existsByLicensePlate(updateRequest.getLicensePlate())).thenReturn(true);

        // Then
        assertThrows(LicensePlateAlreadyExistsException.class, () -> carService.updateCar(carId, updateRequest));

        // Verify
        verify(carRepository).findById(carId);
        verify(carRepository).existsByLicensePlate(updateRequest.getLicensePlate());

    }

    @Test
    void givenUpdateCarRequestWithDifferentUser_whenUserNotFound_thenThrowsException() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String oldUserId = UUID.randomUUID().toString();
        final String newUserId = UUID.randomUUID().toString();

        final UpdateCarRequest updateRequest = UpdateCarRequest.builder()
                .licensePlate("06 TE 456")
                .brand("Hyundai")
                .model("i20")
                .status(CarStatus.ACTIVE)
                .userId(newUserId)
                .build();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("06 OL 456")
                .user(UserEntity.builder().id(oldUserId).build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(newUserId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(carRepository.existsByLicensePlate(updateRequest.getLicensePlate())).thenReturn(false);
        when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        // Then
        assertThrows(UserNotFoundException.class, () -> carService.updateCar(carId, updateRequest));

        // Verify
        verify(userRepository).findById(newUserId);

    }

    @Test
    void givenCarIsNotActive_whenUpdateCar_thenThrowCarStatusNotValidException() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final UpdateCarRequest updateRequest = UpdateCarRequest.builder()
                .licensePlate("34 UP 999")
                .brand("Renault")
                .model("Megane")
                .status(CarStatus.DELETED)
                .userId(userId)
                .build();

        final UserEntity userEntity = UserEntity.builder().id(userId).build();

        final CarEntity deletedCarEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 OLD 888")
                .brand("Toyota")
                .model("Corolla")
                .status(CarStatus.DELETED) // not ACTIVE
                .user(userEntity)
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(deletedCarEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        assertThrows(CarStatusNotValidException.class, () -> carService.updateCar(carId, updateRequest));

        // Verify
        verify(carRepository).findById(carId);
        verify(userIdentity, never()).getUserId();
        verify(userIdentity, never()).getUserType();
        verify(carRepository, never()).save(any(CarEntity.class));

    }


    @Test
    void givenExistingCarAndUserIsOwner_whenDeleteCar_thenStatusUpdatedToDeleted() {

        // Given
        final String userId = UUID.randomUUID().toString();
        final String carId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("34 DE 001")
                .status(CarStatus.ACTIVE)
                .user(UserEntity.builder().id(userId).build())
                .build();

        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // When
        carService.deleteCar(carId);

        // Then
        assertEquals(CarStatus.DELETED, carEntity.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(carRepository).save(carEntity);
        verify(userIdentity).getUserId();
        verify(userIdentity).getUserType();

    }

    @Test
    void givenExistingCarAndUserIsAdmin_whenDeleteCar_thenStatusUpdatedToDeleted() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("06 AD 001")
                .status(CarStatus.ACTIVE)
                .user(UserEntity.builder().id(userId).build())
                .build();

        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserType()).thenReturn(UserType.ADMIN);

        // When
        carService.deleteCar(carId);

        // Then
        assertEquals(CarStatus.DELETED, carEntity.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(carRepository).save(carEntity);
        verify(userIdentity).getUserType();
        verify(userIdentity, times(1)).getUserId();

    }

    @Test
    void givenCarAlreadyDeleted_whenDeleteCar_thenSkipSaving() {

        // Given
        final String userId = UUID.randomUUID().toString();
        final String carId = UUID.randomUUID().toString();

        final CarEntity carEntity = CarEntity.builder()
                .id(carId)
                .licensePlate("07 OL 999")
                .status(CarStatus.DELETED)
                .user(UserEntity.builder().id(userId).build())
                .build();

        when(carRepository.findById(carId)).thenReturn(Optional.of(carEntity));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // When
        carService.deleteCar(carId);

        // Then
        assertEquals(CarStatus.DELETED, carEntity.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(carRepository, never()).save(any());
        verify(userIdentity).getUserId();
        verify(userIdentity).getUserType();

    }

    @Test
    void givenCarNotFound_whenDeleteCar_thenThrowCarNotFoundException() {

        // Given
        final String carId = UUID.randomUUID().toString();

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // Then
        assertThrows(CarNotFoundException.class, () -> carService.deleteCar(carId));

        // Verify
        verify(carRepository).findById(carId);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(userIdentity);

    }

}