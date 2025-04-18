package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.dto.request.services.UpdateServiceRequest;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UpdateServiceRequestToServiceEntityMapper {

    @Named("update")
    default void update(@MappingTarget ServiceEntity service, UpdateServiceRequest request) {
        if (request == null || service == null) return;

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());
    }

    static UpdateServiceRequestToServiceEntityMapper initialize() {
        return Mappers.getMapper(UpdateServiceRequestToServiceEntityMapper.class);
    }
}

