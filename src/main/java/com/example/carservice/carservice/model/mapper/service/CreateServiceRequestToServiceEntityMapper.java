package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.dto.request.services.CreateServiceRequest;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting {@link CreateServiceRequest} to {@link ServiceEntity}.
 * Used when creating new service entries in the system.
 */
@Mapper
public interface CreateServiceRequestToServiceEntityMapper extends BaseMapper<CreateServiceRequest, ServiceEntity> {

    /**
     * Maps a {@link CreateServiceRequest} to a {@link ServiceEntity}.
     *
     * @param request the service creation request DTO
     * @return the service entity ready for persistence
     */
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

    /**
     * Initializes the mapper instance.
     *
     * @return a singleton instance of the mapper
     */
    static CreateServiceRequestToServiceEntityMapper initialize() {
        return Mappers.getMapper(CreateServiceRequestToServiceEntityMapper.class);
    }

}

