package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for applying updates from {@link UpdateCarRequest} to an existing {@link CarEntity}.
 */
@Mapper
public interface UpdateCarRequestToCarEntityMapper {

    /**
     * Updates the target {@link CarEntity} fields using values from the {@link UpdateCarRequest}.
     *
     * @param request the update payload
     * @param entity  the existing car entity to be updated
     * @param user    the user to assign to the car
     */
    @Named("updateEntity")
    default void updateEntity(UpdateCarRequest request, CarEntity entity, UserEntity user) {
        entity.setLicensePlate(request.getLicensePlate());
        entity.setModel(request.getModel());
        entity.setBrand(request.getBrand());
        entity.setStatus(request.getStatus());
        entity.setUser(user);
    }

    /**
     * Initializes the mapper using MapStruct's factory method.
     *
     * @return a singleton mapper instance
     */
    static UpdateCarRequestToCarEntityMapper initialize() {
        return Mappers.getMapper(UpdateCarRequestToCarEntityMapper.class);
    }

}

