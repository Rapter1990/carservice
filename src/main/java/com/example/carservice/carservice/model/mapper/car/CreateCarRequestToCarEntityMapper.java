package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for converting {@link CreateCarRequest} to {@link CarEntity}.
 */
@Mapper
public interface CreateCarRequestToCarEntityMapper extends BaseMapper<CreateCarRequest, CarEntity> {

    /**
     * Maps a create car request and associated user to a new {@link CarEntity}.
     *
     * @param request    the car creation request DTO
     * @param userEntity the user to whom the car is assigned
     * @return the mapped car entity
     */
    @Named("mapForSaving")
    default CarEntity mapForSaving(CreateCarRequest request, UserEntity userEntity) {
        return CarEntity.builder()
                .licensePlate(request.getLicensePlate())
                .model(request.getModel())
                .brand(request.getBrand())
                .user(userEntity)
                .status(CarStatus.ACTIVE)
                .build();
    }

    /**
     * Initializes the mapper using MapStruct's factory method.
     *
     * @return a singleton mapper instance
     */
    static CreateCarRequestToCarEntityMapper initialize() {
        return Mappers.getMapper(CreateCarRequestToCarEntityMapper.class);
    }

}
