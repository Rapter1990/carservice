package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.CarNotFoundException;
import com.example.carservice.carservice.exception.ServiceCarMismatchException;
import com.example.carservice.carservice.exception.ServiceNotFoundException;
import com.example.carservice.carservice.exception.ServiceTitleAlreadyExistsException;
import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.request.services.AssignServiceToCarRequest;
import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.ListServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.UpdateServiceRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.carservice.model.mapper.service.CreateServiceRequestToServiceEntityMapper;
import com.example.carservice.carservice.model.mapper.service.ServiceEntityToServiceDtoMapper;
import com.example.carservice.carservice.model.mapper.service.UpdateServiceRequestToServiceEntityMapper;
import com.example.carservice.carservice.repository.CarRepository;
import com.example.carservice.carservice.repository.ServiceRepository;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.CustomPaging;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicesToCarServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private ServicesToCarServiceImpl servicesToCarService;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserIdentity userIdentity;

    private final CreateServiceRequestToServiceEntityMapper createServiceRequestToServiceEntityMapper = CreateServiceRequestToServiceEntityMapper.initialize();
    private final ServiceEntityToServiceDtoMapper serviceEntityToServiceDtoMapper = ServiceEntityToServiceDtoMapper.initialize();
    private final UpdateServiceRequestToServiceEntityMapper updateServiceRequestToServiceEntityMapper = UpdateServiceRequestToServiceEntityMapper.initialize();

    @Test
    void givenValidRequest_whenCreateService_thenReturnServiceDto() {

        // Given
        final CreateServiceRequest request = CreateServiceRequest.builder()
                .title("Oil Change")
                .description("Change the engine oil")
                .status(ServiceStatus.CREATED)
                .build();

        final ServiceEntity savedEntity = createServiceRequestToServiceEntityMapper.map(request);

        final ServiceDto expected = serviceEntityToServiceDtoMapper.map(savedEntity);

        // When
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(savedEntity);

        final ServiceDto result = servicesToCarService.createService(request);

        // Then
        assertNotNull(result);
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getStatus(), result.getStatus());

        // Verify
        verify(serviceRepository).save(any(ServiceEntity.class));

    }

    @Test
    void givenDuplicateTitle_whenCreateService_thenThrow() {
        // Given
        final String duplicateTitle = "Oil Change";

        final CreateServiceRequest request = CreateServiceRequest.builder()
                .title(duplicateTitle)
                .description("Check oil level and replace")
                .status(ServiceStatus.CREATED)
                .build();

        when(serviceRepository.existsByTitle(duplicateTitle)).thenReturn(true);

        // Then
        assertThrows(ServiceTitleAlreadyExistsException.class,
                () -> servicesToCarService.createService(request));

        // Verify
        verify(serviceRepository).existsByTitle(duplicateTitle);
        verify(serviceRepository, never()).save(any(ServiceEntity.class));
    }


    @Test
    void whenGetAllServices_thenReturnPagedServiceList() {

        // Given
        final ServiceEntity service = ServiceEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Maintenance")
                .description("Regular maintenance")
                .status(ServiceStatus.CREATED)
                .build();

        final Page<ServiceEntity> page = new PageImpl<>(List.of(service));

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        // When
        when(serviceRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Then
        CustomPage<ServiceDto> result = servicesToCarService.getAllServices(pagingRequest);

        assertEquals(1, result.getContent().size());
        ServiceDto actual = result.getContent().get(0);
        assertEquals(service.getId(), actual.getId());
        assertEquals(service.getTitle(), actual.getTitle());
        assertEquals(service.getDescription(), actual.getDescription());
        assertEquals(service.getStatus(), actual.getStatus());

        // Verify
        verify(serviceRepository).findAll(any(Pageable.class));

    }

    @Test
    void givenCarId_whenGetServicesByCarIdPaged_thenReturnPagedServiceList() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CarEntity car = CarEntity.builder()
                .id(carId)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final ServiceEntity service = ServiceEntity.builder()
                .id("svc1")
                .title("Wash")
                .description("Exterior wash")
                .status(ServiceStatus.DONE)
                .car(car)
                .build();

        final Page<ServiceEntity> page = new PageImpl<>(List.of(service));

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(serviceRepository.findByCarId(eq(carId), any(Pageable.class))).thenReturn(page);

        // Then
        CustomPage<ServiceDto> result = servicesToCarService.getServicesByCarId(carId, pagingRequest);

        assertEquals(1, result.getContent().size());
        ServiceDto actual = result.getContent().get(0);
        assertEquals(service.getId(), actual.getId());
        assertEquals(service.getTitle(), actual.getTitle());
        assertEquals(service.getDescription(), actual.getDescription());
        assertEquals(service.getStatus(), actual.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(serviceRepository).findByCarId(eq(carId), any(Pageable.class));

    }

    @Test
    void givenInvalidCarId_whenGetServicesByCarId_thenThrow() {

        // Given
        final String invalidCarId = "invalid-id";

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        // When
        when(carRepository.findById(invalidCarId)).thenReturn(Optional.empty());

        // Then
        assertThrows(CarNotFoundException.class, () -> servicesToCarService.getServicesByCarId(invalidCarId, pagingRequest));

        // Verify
        verify(carRepository).findById(invalidCarId);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(serviceRepository);

    }

    @Test
    void givenValidRequest_whenAssignServiceToCar_thenReturnDto() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();
        final String serviceId = UUID.randomUUID().toString();

        final AssignServiceToCarRequest request = AssignServiceToCarRequest.builder()
                .carId(carId)
                .serviceId(serviceId)
                .build();

        final CarEntity car = CarEntity.builder()
                .id(carId)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final ServiceEntity service = ServiceEntity.builder()
                .id(serviceId)
                .title("Replace Tires")
                .description("Replace all four tires")
                .status(ServiceStatus.CREATED)
                .build();

        final ServiceDto expected = serviceEntityToServiceDtoMapper.map(service);

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(service);

        // Then
        final ServiceDto result = servicesToCarService.assignServiceToCar(request);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getStatus(), result.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository).save(service);

    }

    @Test
    void givenInvalidCarId_whenAssignServiceToCar_thenThrow() {

        // Given
        final String invalidCarId = "non-existent";
        final String serviceId = "svc";

        final AssignServiceToCarRequest request = AssignServiceToCarRequest.builder()
                .carId(invalidCarId)
                .serviceId(serviceId)
                .build();

        // When
        when(carRepository.findById(invalidCarId)).thenReturn(Optional.empty());

        // Then
        assertThrows(CarNotFoundException.class, () -> servicesToCarService.assignServiceToCar(request));

        // Verify
        verify(carRepository).findById(invalidCarId);
        verifyNoMoreInteractions(carRepository);
        verifyNoInteractions(serviceRepository);

    }

    @Test
    void givenInvalidServiceId_whenAssignServiceToCar_thenThrow() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String userId = "user";
        final String missingServiceId = "missing-id";

        final CarEntity car = CarEntity.builder()
                .id(carId)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final AssignServiceToCarRequest request = AssignServiceToCarRequest.builder()
                .carId(carId)
                .serviceId(missingServiceId)
                .build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findById(missingServiceId)).thenReturn(Optional.empty());
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        assertThrows(ServiceNotFoundException.class, () -> servicesToCarService.assignServiceToCar(request));

        // Verify
        verify(carRepository).findById(carId);
        verify(serviceRepository).findById(missingServiceId);
        verify(userIdentity).getUserId();
        verify(userIdentity).getUserType();

        verifyNoMoreInteractions(carRepository);
        verifyNoMoreInteractions(serviceRepository);

    }

    @Test
    void givenValidRequest_whenUpdateService_thenReturnUpdatedDto() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final String serviceId = UUID.randomUUID().toString();
        final String userId = UUID.randomUUID().toString();

        final CarEntity car = CarEntity.builder()
                .id(carId)
                .user(UserEntity.builder().id(userId).build())
                .build();

        final ServiceEntity service = ServiceEntity.builder()
                .id(serviceId)
                .title("Old Title")
                .description("Old Description")
                .car(car)
                .status(ServiceStatus.CREATED)
                .build();

        final UpdateServiceRequest request = UpdateServiceRequest.builder()
                .title("New Title")
                .description("Updated Description")
                .status(ServiceStatus.PENDING)
                .build();

        updateServiceRequestToServiceEntityMapper.update(service, request);

        final ServiceDto expected = serviceEntityToServiceDtoMapper.map(service);

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(ServiceEntity.class))).thenReturn(service);
        when(userIdentity.getUserId()).thenReturn(userId);
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        ServiceDto result = servicesToCarService.updateServiceByCarId(carId, serviceId, request);

        assertNotNull(result);
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getStatus(), result.getStatus());

        // Verify
        verify(carRepository).findById(carId);
        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository).save(service);

    }

    @Test
    void givenServiceNotInCar_whenUpdate_thenThrow() {

        final String carId = UUID.randomUUID().toString();
        final String serviceId = UUID.randomUUID().toString();

        final CarEntity car = CarEntity.builder()
                .id(carId)
                .user(UserEntity.builder().id(UUID.randomUUID().toString()).build())
                .build();

        final ServiceEntity service = ServiceEntity.builder()
                .id(serviceId)
                .car(CarEntity.builder().id(UUID.randomUUID().toString()).build())
                .build();

        final UpdateServiceRequest req = UpdateServiceRequest.builder()
                .title("x").status(ServiceStatus.IN_PROGRESS).build();

        // When
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(userIdentity.getUserId()).thenReturn(car.getUser().getId());
        when(userIdentity.getUserType()).thenReturn(UserType.USER);

        // Then
        assertThrows(ServiceCarMismatchException.class, () -> servicesToCarService.updateServiceByCarId(carId, serviceId, req));

        // Verify
        verify(carRepository).findById(carId);
        verify(serviceRepository).findById(serviceId);
        verify(userIdentity).getUserId();
        verify(userIdentity).getUserType();

        verifyNoMoreInteractions(carRepository);
        verifyNoMoreInteractions(serviceRepository);

    }

    @Test
    void givenFilterWithCarIdAndStatus_whenGetServicesPaged_thenReturnFilteredPagedServices() {

        // Given
        final String carId = UUID.randomUUID().toString();
        final ServiceStatus status = ServiceStatus.DONE;

        final ServiceEntity service = ServiceEntity.builder()
                .id(UUID.randomUUID().toString())
                .title("Brake Check")
                .description("Inspect and replace brake pads")
                .status(status)
                .car(CarEntity.builder().id(carId).build())
                .build();

        final Page<ServiceEntity> page = new PageImpl<>(List.of(service));

        ListServiceRequest.Filter filter = new ListServiceRequest.Filter();
        filter.setCarId(Optional.of(carId));
        filter.setStatus(Optional.of(status));

        ListServiceRequest request = new ListServiceRequest();
        request.setFilter(filter);

        final CustomPagingRequest pagingRequest = CustomPagingRequest.builder()
                .pagination(CustomPaging.builder()
                        .pageNumber(1)
                        .pageSize(10)
                        .build())
                .build();

        // When
        when(serviceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // Then
        CustomPage<ServiceDto> result = servicesToCarService.getServices(request, pagingRequest);

        assertEquals(1, result.getContent().size());
        ServiceDto actual = result.getContent().get(0);
        assertEquals(service.getId(), actual.getId());
        assertEquals(service.getTitle(), actual.getTitle());
        assertEquals(service.getDescription(), actual.getDescription());
        assertEquals(service.getStatus(), actual.getStatus());

        // Verify
        verify(serviceRepository).findAll(any(Specification.class), any(Pageable.class));

    }


}