package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.model.UserIdentity;
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
import com.example.carservice.carservice.model.mapper.service.CreateServiceRequestToServiceEntityMapper;
import com.example.carservice.carservice.model.mapper.service.ServiceEntityToServiceDtoMapper;
import com.example.carservice.carservice.model.mapper.service.UpdateServiceRequestToServiceEntityMapper;
import com.example.carservice.carservice.repository.CarRepository;
import com.example.carservice.carservice.repository.ServiceRepository;
import com.example.carservice.carservice.service.ServicesToCarService;
import com.example.carservice.carservice.utils.UserPermissionUtils;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicesToCarServiceImpl implements ServicesToCarService {

    private final ServiceRepository serviceRepository;
    private final CarRepository carRepository;
    private final UserIdentity userIdentity;

    private final CreateServiceRequestToServiceEntityMapper createServiceRequestToServiceEntityMapper = CreateServiceRequestToServiceEntityMapper.initialize();
    private final ServiceEntityToServiceDtoMapper serviceEntityToServiceDtoMapper = ServiceEntityToServiceDtoMapper.initialize();

    private final UpdateServiceRequestToServiceEntityMapper updateServiceRequestToServiceEntityMapper = UpdateServiceRequestToServiceEntityMapper.initialize();


    @Override
    @Transactional(readOnly = true)
    public CustomPage<ServiceDto> getAllServices(CustomPagingRequest pagingRequest) {
        Page<ServiceEntity> page = serviceRepository.findAll(pagingRequest.toPageable());

        List<ServiceDto> services = page.getContent()
                .stream()
                .map(serviceEntityToServiceDtoMapper::map)
                .toList();

        return CustomPage.of(services, page);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomPage<ServiceDto> getServicesByCarId(String carId, CustomPagingRequest pagingRequest) {
        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        UserPermissionUtils.checkAccessPermission(userIdentity, car.getUser().getId());

        Page<ServiceEntity> page = serviceRepository.findByCarId(carId, pagingRequest.toPageable());

        List<ServiceDto> services = page.getContent()
                .stream()
                .map(serviceEntityToServiceDtoMapper::map)
                .toList();

        return CustomPage.of(services, page);

    }

    @Override
    @Transactional
    public ServiceDto createService(CreateServiceRequest request) {
        if (serviceRepository.existsByTitle(request.getTitle())) {
            throw new ServiceTitleAlreadyExistsException(request.getTitle());
        }

        ServiceEntity service = createServiceRequestToServiceEntityMapper.map(request);
        ServiceEntity savedService = serviceRepository.save(service);
        return serviceEntityToServiceDtoMapper.map(savedService);
    }


    @Override
    @Transactional
    public ServiceDto assignServiceToCar(AssignServiceToCarRequest request) {

        CarEntity car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new CarNotFoundException(request.getCarId()));

        UserPermissionUtils.checkAccessPermission(userIdentity, car.getUser().getId());

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException(request.getServiceId()));

        service.setCar(car);

        ServiceEntity savedSServiceEntity = serviceRepository.save(service);

        return serviceEntityToServiceDtoMapper.map(savedSServiceEntity);

    }

    @Override
    @Transactional
    public ServiceDto updateServiceByCarId(String carId, String serviceId, UpdateServiceRequest request) {

        CarEntity car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        UserPermissionUtils.checkAccessPermission(userIdentity, car.getUser().getId());

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));

        if (service.getCar() == null || !service.getCar().getId().equals(carId)) {
            throw new ServiceCarMismatchException(carId, serviceId);
        }

        updateServiceRequestToServiceEntityMapper.update(service, request);

        ServiceEntity updatedService = serviceRepository.save(service);

        return serviceEntityToServiceDtoMapper.map(updatedService);

    }

    @Override
    @Transactional(readOnly = true)
    public CustomPage<ServiceDto> getServices(ListServiceRequest request, CustomPagingRequest pagingRequest) {
        Page<ServiceEntity> page = serviceRepository.findAll(request.toSpecification(), pagingRequest.toPageable());

        List<ServiceDto> services = page.getContent()
                .stream()
                .map(serviceEntityToServiceDtoMapper::map)
                .toList();

        return CustomPage.of(services, page);
    }

}
