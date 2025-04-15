package com.example.carservice.carservice.service;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;

public interface CarService {

    Car assignCarToUser(final CreateCarRequest createCarRequest);

}
