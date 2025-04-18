package com.example.carservice.carservice.model;

import com.example.carservice.auth.model.User;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.common.model.BaseDomainModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Domain model representing a car and its associated user and services.
 * Includes car-specific attributes such as license plate, model, and brand.
 * Also contains a list of related services and user information.
 * Inherits audit metadata from {@link BaseDomainModel}.
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Car extends BaseDomainModel {

    private String id;
    private String licensePlate;
    private String model;
    private String brand;
    private String userId;
    private CarStatus status;
    private User user;
    private List<ServiceDto> serviceList;

}
