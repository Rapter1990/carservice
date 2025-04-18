package com.example.carservice.carservice.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.request.services.AssignServiceToCarRequest;
import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.ListServiceRequest;
import com.example.carservice.carservice.model.dto.request.services.UpdateServiceRequest;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;

import java.util.List;

/**
 * Service interface for managing operations related to car services (maintenance, repair, etc.).
 * Includes functionalities such as creating, updating, assigning, and listing services
 * associated with cars.
 */
public interface ServicesToCarService {

    /**
     * Retrieves a paginated list of all services in the system.
     *
     * @param pagingRequest the pagination and sorting parameters
     * @return a {@link CustomPage} containing {@link ServiceDto} entries
     */
    CustomPage<ServiceDto> getAllServices(CustomPagingRequest pagingRequest);

    /**
     * Retrieves a paginated list of services associated with a specific car ID.
     *
     * @param carId the ID of the car
     * @param pagingRequest the pagination and sorting parameters
     * @return a {@link CustomPage} containing services of the specified car
     */
    CustomPage<ServiceDto> getServicesByCarId(String carId, CustomPagingRequest pagingRequest);

    /**
     * Creates a new service entry.
     *
     * @param request the request payload containing service details
     * @return the created {@link ServiceDto}
     */
    ServiceDto createService(CreateServiceRequest request);

    /**
     * Assigns an existing service to a car.
     *
     * @param request the assignment request containing service and car IDs
     * @return the updated {@link ServiceDto} after assignment
     */
    ServiceDto assignServiceToCar(AssignServiceToCarRequest request);

    /**
     * Updates a service assigned to a specific car.
     *
     * @param carId the ID of the car
     * @param serviceId the ID of the service
     * @param request the update payload
     * @return the updated {@link ServiceDto}
     */
    ServiceDto updateServiceByCarId(String carId, String serviceId, UpdateServiceRequest request);

    /**
     * Retrieves services filtered by request parameters and paginated.
     *
     * @param request filtering parameters such as status, carId, etc.
     * @param pagingRequest the pagination and sorting parameters
     * @return a paginated list of matching {@link ServiceDto}s
     */
    CustomPage<ServiceDto> getServices(ListServiceRequest request, CustomPagingRequest pagingRequest);

}
