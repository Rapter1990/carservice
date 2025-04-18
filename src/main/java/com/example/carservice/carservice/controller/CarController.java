package com.example.carservice.carservice.controller;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.mapper.car.CarToCarResponseMapper;
import com.example.carservice.carservice.model.mapper.car.CustomPageCarToCustomPagingCarResponseMapper;
import com.example.carservice.carservice.service.CarService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing cars.
 * Provides endpoints for creating, retrieving, updating, and soft-deleting cars.
 * Supports role-based access control (USER and ADMIN) and pagination.
 */
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Validated
@Tag(name = "Car Management",
        description = "Handles car creation, updates, and retrieval.")
public class CarController {

    private final CarService carService;
    private final CarToCarResponseMapper carToCarResponseMapper = CarToCarResponseMapper.initialize();

    private final CustomPageCarToCustomPagingCarResponseMapper carPageMapper = CustomPageCarToCustomPagingCarResponseMapper.initialize();

    /**
     * Assigns a new car to a user.
     *
     * @param createCarRequest the request payload containing car details
     * @return a {@link CustomResponse} containing the created car's data
     */
    @Operation(
            summary = "Assign Car to User",
            description = "Creates a car and assigns it to a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "409", description = "License plate already exists")
            }
    )
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping
    public CustomResponse<CarResponse> assignCarToUser(@RequestBody @Valid final CreateCarRequest createCarRequest) {

        Car createdCar = carService.assignCarToUser(createCarRequest);
        CarResponse response = carToCarResponseMapper.mapToResponse(createdCar);
        return CustomResponse.successOf(response);

    }

    /**
     * Retrieves a car by ID.
     *
     * @param carId the ID of the car to retrieve
     * @return a {@link CustomResponse} containing the car's information
     */
    @Operation(
            summary = "Get Car by ID",
            description = "Retrieves a car by ID if the user is the owner or an admin.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car found"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Car not found")
            }
    )
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/{carId}")
    public CustomResponse<CarResponse> getCarById(@PathVariable @Valid @UUID final String carId) {

        Car car = carService.getCarById(carId);
        CarResponse response = carToCarResponseMapper.mapToResponse(car);
        return CustomResponse.successOf(response);

    }

    /**
     * Retrieves all cars assigned to a specific user.
     *
     * @param userId the ID of the user
     * @param pagingRequest the pagination parameters
     * @return paginated response of cars
     */
    @Operation(summary = "Get all cars by user",
            description = "Retrieves a paginated list of cars assigned to a specific user (admin or self).")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's car list")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping("/users/{userId}")
    public CustomResponse<CustomPagingResponse<CarResponse>> getAllCarsByUser(
            @PathVariable @Valid @UUID final String userId,
            @RequestBody @Valid final CustomPagingRequest pagingRequest) {

        CustomPage<Car> customPage = carService.getAllCarsByUser(userId, pagingRequest);
        CustomPagingResponse<CarResponse> pagingResponse = carPageMapper.toPagingResponse(customPage);
        return CustomResponse.successOf(pagingResponse);

    }

    /**
     * Retrieves all cars in the system (Admin only).
     *
     * @param pagingRequest pagination parameters
     * @return paginated response of all cars
     */
    @Operation(summary = "Get all cars (admin only)",
            description = "Retrieves a paginated list of all cars in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved car list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/all")
    public CustomResponse<CustomPagingResponse<CarResponse>> getAllCars(
            @RequestBody @Valid final CustomPagingRequest pagingRequest) {

        CustomPage<Car> customPage = carService.getAllCars(pagingRequest);
        CustomPagingResponse<CarResponse> pagingResponse = carPageMapper.toPagingResponse(customPage);
        return CustomResponse.successOf(pagingResponse);

    }

    /**
     * Retrieves all active cars in the system (Admin only).
     *
     * @param pagingRequest pagination parameters
     * @return paginated response of active cars
     */
    @Operation(summary = "Get all cars by its status active(admin only)",
            description = "Retrieves a paginated list of all cars in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved car list")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/allcarsByActiveStatus")
    public CustomResponse<CustomPagingResponse<CarResponse>> getAllCarsByActiveStatus(
            @RequestBody @Valid final CustomPagingRequest pagingRequest) {

        CustomPage<Car> customPage = carService.getAllCarsByStatus(pagingRequest);
        CustomPagingResponse<CarResponse> pagingResponse = carPageMapper.toPagingResponse(customPage);
        return CustomResponse.successOf(pagingResponse);

    }

    /**
     * Updates car information by ID.
     *
     * @param carId the ID of the car to update
     * @param updateCarRequest the updated car details
     * @return the updated car data
     */
    @Operation(summary = "Update Car",
            description = "Updates a car's details, including its status and ownership.")
    @ApiResponse(responseCode = "200", description = "Car updated successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Car not found")
    @ApiResponse(responseCode = "409", description = "License plate already exists")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PutMapping("/{carId}")
    public CustomResponse<CarResponse> updateCar(
            @PathVariable @Valid @UUID final String carId,
            @RequestBody @Valid final UpdateCarRequest updateCarRequest) {

        Car updatedCar = carService.updateCar(carId, updateCarRequest);
        CarResponse response = carToCarResponseMapper.mapToResponse(updatedCar);
        return CustomResponse.successOf(response);

    }

    /**
     * Marks a car as deleted (soft delete).
     *
     * @param carId the ID of the car to delete
     * @return success message
     */
    @Operation(
            summary = "Soft Delete Car",
            description = "Marks a car as DELETED by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car soft deleted"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Car not found")
            }
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{carId}")
    public CustomResponse<String> deleteCar(@PathVariable @Valid @UUID final String carId) {
        carService.deleteCar(carId);
        return CustomResponse.successOf("Car with ID " + carId + " is deleted");
    }

}

