package com.example.carservice.carservice.model;

import com.example.carservice.auth.model.User;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.common.model.BaseDomainModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
