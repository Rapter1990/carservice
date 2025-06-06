package com.example.carservice.carservice.controller;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.request.services.*;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.carservice.model.mapper.service.CustomPageServiceDtoToCustomPagingServiceResponseMapper;
import com.example.carservice.carservice.model.mapper.service.ServiceDtoToServiceResponseMapper;
import com.example.carservice.carservice.service.ServicesToCarService;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import com.example.carservice.common.model.dto.response.CustomPagingResponse;
import com.example.carservice.common.model.dto.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller for managing car services.
 * Provides endpoints for service creation, assignment, updating, and querying.
 * Restricted to users with ADMIN authority.
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Service Management",
        description = "Handles service creation, assignment, updates, and queries.")
public class ServicesController {

    private final ServicesToCarService servicesToCarService;

    private final ServiceDtoToServiceResponseMapper serviceDtoToServiceResponseMapper = ServiceDtoToServiceResponseMapper.initialize();

    private final CustomPageServiceDtoToCustomPagingServiceResponseMapper customPageServiceDtoToCustomPagingServiceResponseMapper
            = CustomPageServiceDtoToCustomPagingServiceResponseMapper.initialize();

    /**
     * Retrieves a paginated list of all services.
     *
     * @param pagingRequest the pagination parameters
     * @return a paginated list of all services
     */
    @Operation(summary = "Get All Services (Paged)", description = "Returns all service records with pagination. Admin only.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all services")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/all")
    public CustomResponse<CustomPagingResponse<ServiceResponse>> getAllServices(@RequestBody @Valid final CustomPagingRequest pagingRequest) {
        CustomPage<ServiceDto> allServicesWithPagination = servicesToCarService.getAllServices(pagingRequest);
        CustomPagingResponse<ServiceResponse> pagingResponse = customPageServiceDtoToCustomPagingServiceResponseMapper.toPagingResponse(allServicesWithPagination);
        return CustomResponse.successOf(pagingResponse);
    }

    /**
     * Retrieves a paginated list of services filtered by a specific car ID.
     *
     * @param carId the car ID to filter by
     * @param pagingRequest the pagination parameters
     * @return a paginated list of services for the given car
     */
    @Operation(summary = "Get Services by Car ID (Paged)",
            description = "Returns service records filtered by car ID. Admin only.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved services for the given car")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/car/{carId}")
    public CustomResponse<CustomPagingResponse<ServiceResponse>> getServicesByCar(
            @PathVariable @Valid @UUID final String carId,
            @RequestBody @Valid final CustomPagingRequest pagingRequest) {
        CustomPage<ServiceDto> servicesByCarId = servicesToCarService.getServicesByCarId(carId, pagingRequest);
        CustomPagingResponse<ServiceResponse> pagingResponse = customPageServiceDtoToCustomPagingServiceResponseMapper.toPagingResponse(servicesByCarId);
        return CustomResponse.successOf(pagingResponse);
    }

    /**
     * Filters services using criteria such as car ID and status, and returns paginated results.
     *
     * @param request the filter and paging request
     * @return paginated and filtered list of services
     */
    @Operation(summary = "Filter Services", description = "Filters services by car ID and/or status. Admin only.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered services")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/filter")
    public CustomResponse<CustomPagingResponse<ServiceResponse>> getFilteredServices(
            @RequestBody @Valid final FilterServicePagingRequest request) {

        CustomPage<ServiceDto> servicesWithPagination = servicesToCarService.getServices(
                request.getFilterRequest(), request.getPagingRequest());

        CustomPagingResponse<ServiceResponse> pagingResponse =
                customPageServiceDtoToCustomPagingServiceResponseMapper.toPagingResponse(servicesWithPagination);

        return CustomResponse.successOf(pagingResponse);
    }

    /**
     * Creates a new service entry.
     *
     * @param request the request body containing service details
     * @return the created service
     */
    @Operation(summary = "Create a New Service", description = "Creates a new service. Admin only.")
    @ApiResponse(responseCode = "201", description = "Service created successfully")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public CustomResponse<ServiceResponse> createService(@RequestBody @Valid final CreateServiceRequest request) {
        ServiceDto createdServiceDto = servicesToCarService.createService(request);
        ServiceResponse createdServiceResponse = serviceDtoToServiceResponseMapper.map(createdServiceDto);
        return CustomResponse.successOf(createdServiceResponse);
    }

    /**
     * Assigns an existing service to a car.
     *
     * @param request the request containing car ID and service ID
     * @return the updated service after assignment
     */
    @Operation(summary = "Assign Service to Car", description = "Assigns an existing service to a car. Admin only.")
    @ApiResponse(responseCode = "200", description = "Service successfully assigned to car")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/assign")
    public CustomResponse<ServiceResponse> assignServiceToCar(@RequestBody @Valid final AssignServiceToCarRequest request) {
        ServiceDto assignedServiceDto = servicesToCarService.assignServiceToCar(request);
        ServiceResponse assignedServiceResponse = serviceDtoToServiceResponseMapper.map(assignedServiceDto);
        return CustomResponse.successOf(assignedServiceResponse);
    }

    /**
     * Updates a service that is assigned to a specific car.
     *
     * @param carId the car ID
     * @param serviceId the service ID
     * @param request the request containing updated service details
     * @return the updated service
     */
    @Operation(summary = "Update Service by Car ID", description = "Updates an existing service assigned to a car. Admin only.")
    @ApiResponse(responseCode = "200", description = "Service updated successfully")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/car/{carId}/service/{serviceId}")
    public CustomResponse<ServiceResponse> updateServiceByCar(
            @PathVariable @Valid @UUID final String carId,
            @PathVariable @Valid @UUID final String serviceId,
            @RequestBody @Valid final UpdateServiceRequest request) {
        ServiceDto updatedServiceDto = servicesToCarService.updateServiceByCarId(carId, serviceId, request);
        ServiceResponse updatedServiceResponse = serviceDtoToServiceResponseMapper.map(updatedServiceDto);
        return CustomResponse.successOf(updatedServiceResponse);
    }

}
