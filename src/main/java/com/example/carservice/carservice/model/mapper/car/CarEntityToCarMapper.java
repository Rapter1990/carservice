package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.mapper.UserEntityToUserMapper;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.mapper.service.ServiceEntityToServiceDtoMapper;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper interface for converting {@link CarEntity} to {@link Car} domain model.
 * Uses mappers for nested objects: {@link UserEntityToUserMapper} and {@link ServiceEntityToServiceDtoMapper}.
 */
@Mapper(uses = {
        UserEntityToUserMapper.class,
        ServiceEntityToServiceDtoMapper.class
})
public interface CarEntityToCarMapper extends BaseMapper<CarEntity, Car> {

    /**
     * Maps a {@link CarEntity} to a {@link Car} including nested user and service details.
     *
     * @param entity the car entity from the database
     * @return a fully populated domain model representation
     */
    @Named("mapFromEntity")
    default Car mapFromEntity(CarEntity entity) {
        UserEntityToUserMapper userMapper = UserEntityToUserMapper.initialize();
        ServiceEntityToServiceDtoMapper serviceMapper = ServiceEntityToServiceDtoMapper.initialize();

        return Car.builder()
                .id(entity.getId())
                .licensePlate(entity.getLicensePlate())
                .model(entity.getModel())
                .brand(entity.getBrand())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .status(entity.getStatus())
                .user(entity.getUser() != null ? userMapper.map(entity.getUser()) : null)
                .serviceList(entity.getServices() != null
                        ? entity.getServices().stream()
                        .map(serviceMapper::map)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    /**
     * Initializes the mapper using MapStruct's factory method.
     *
     * @return a singleton mapper instance
     */
    static CarEntityToCarMapper initialize() {
        return Mappers.getMapper(CarEntityToCarMapper.class);
    }

}

