package com.example.carservice.carservice.service;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;

public interface CarService {

    Car assignCarToUser(final CreateCarRequest createCarRequest);

    Car getCarById(final String carId);

    CustomPage<Car> getAllCars(final CustomPagingRequest pagingRequest);

    CustomPage<Car> getAllCarsByUser(final String userId, final CustomPagingRequest pagingRequest);

}
