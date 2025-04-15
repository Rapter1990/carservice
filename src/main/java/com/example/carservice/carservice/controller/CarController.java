package com.example.carservice.carservice.controller;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.mapper.car.CarToCarResponseMapper;
import com.example.carservice.carservice.service.CarService;
import com.example.carservice.common.model.dto.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
@Validated
@Tag(name = "Car Management", description = "Handles car creation, updates, and retrieval.")
public class CarController {

    private final CarService carService;
    private final CarToCarResponseMapper carToCarResponseMapper = CarToCarResponseMapper.initialize();

    /**
     * Endpoint to assign a new car to user.
     *
     * @param createCarRequest The {@link CreateCarRequest} object containing car details.
     * @return A {@link CustomResponse} containing the created car information.
     */
    @Operation(
            summary = "Create a new car",
            description = "Creates a car and assigns it to a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "409", description = "License plate already exists")
            }
    )
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping
    public CustomResponse<CarResponse> createCar(@RequestBody @Valid final CreateCarRequest createCarRequest) {
        Car createdCar = carService.assignCarToUser(createCarRequest);
        CarResponse response = carToCarResponseMapper.mapToResponse(createdCar);
        return CustomResponse.successOf(response);
    }
}

