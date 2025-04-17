package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateCarRequestToCarEntityMapper {

    /**
     * Applies updates from UpdateCarRequest to an existing CarEntity.
     */
    @Named("updateEntity")
    default void updateEntity(UpdateCarRequest request, CarEntity entity, UserEntity user) {
        entity.setLicensePlate(request.getLicensePlate());
        entity.setModel(request.getModel());
        entity.setBrand(request.getBrand());
        entity.setStatus(request.getStatus());
        entity.setUser(user);
    }

    static UpdateCarRequestToCarEntityMapper initialize() {
        return Mappers.getMapper(UpdateCarRequestToCarEntityMapper.class);
    }

}

