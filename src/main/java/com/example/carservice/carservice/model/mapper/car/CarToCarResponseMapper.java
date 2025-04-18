package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.mapper.UserToUserResponseMapper;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.response.CarResponse;
import com.example.carservice.carservice.model.mapper.service.ServiceDtoToServiceResponseMapper;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Mapper(uses = {
        UserToUserResponseMapper.class,
        ServiceDtoToServiceResponseMapper.class
})
public interface CarToCarResponseMapper extends BaseMapper<Car, CarResponse> {

    @Named("mapToResponse")
    default CarResponse mapToResponse(Car car) {
        UserToUserResponseMapper userMapper = UserToUserResponseMapper.initialize();
        ServiceDtoToServiceResponseMapper serviceMapper = ServiceDtoToServiceResponseMapper.initialize();

        return CarResponse.builder()
                .id(car.getId())
                .licensePlate(car.getLicensePlate())
                .model(car.getModel())
                .brand(car.getBrand())
                .userId(car.getUserId())
                .status(car.getStatus())
                .user(car.getUser() != null ? userMapper.map(car.getUser()) : null)
                .serviceList(car.getServiceList() != null
                        ? car.getServiceList().stream()
                        .map(serviceMapper::map)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    static CarToCarResponseMapper initialize() {
        return Mappers.getMapper(CarToCarResponseMapper.class);
    }
}

