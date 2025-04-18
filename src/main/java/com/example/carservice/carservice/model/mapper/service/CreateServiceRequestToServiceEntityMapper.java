package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateServiceRequestToServiceEntityMapper extends BaseMapper<CreateServiceRequest, ServiceEntity> {

    @Named("map")
    default ServiceEntity map(CreateServiceRequest request) {
        if (request == null) {
            return null;
        }

        return ServiceEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .build();
    }

    static CreateServiceRequestToServiceEntityMapper initialize() {
        return Mappers.getMapper(CreateServiceRequestToServiceEntityMapper.class);
    }

}

