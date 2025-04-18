package com.example.carservice.carservice.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.request.services.AssignServiceToCarRequest;
import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.ListServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.UpdateServiceRequest;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;

import java.util.List;

public interface ServicesToCarService {

    CustomPage<ServiceDto> getAllServices(CustomPagingRequest pagingRequest);

    CustomPage<ServiceDto> getServicesByCarId(String carId, CustomPagingRequest pagingRequest);

    ServiceDto createService(CreateServiceRequest request);

    ServiceDto assignServiceToCar(AssignServiceToCarRequest request);

    ServiceDto updateServiceByCarId(String carId, String serviceId, UpdateServiceRequest request);

    CustomPage<ServiceDto> getServices(ListServiceRequest request, CustomPagingRequest pagingRequest);

}
