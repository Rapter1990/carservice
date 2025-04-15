package com.example.carservice.carservice.model.mapper.car;

import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateCarRequestToCarEntityMapper extends BaseMapper<CreateCarRequest, CarEntity> {

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

    static CreateCarRequestToCarEntityMapper initialize() {
        return Mappers.getMapper(CreateCarRequestToCarEntityMapper.class);
    }
}
