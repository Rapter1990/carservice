package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.dto.request.services.UpdateServiceRequest;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for applying updates from {@link UpdateServiceRequest}
 * to an existing {@link ServiceEntity}.
 */
@Mapper
public interface UpdateServiceRequestToServiceEntityMapper {

    /**
     * Applies the update request to the target service entity.
     *
     * @param service the entity to be updated
     * @param request the update request DTO
     */
    @Named("update")
    default void update(@MappingTarget ServiceEntity service, UpdateServiceRequest request) {
        if (request == null || service == null) return;

        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setStatus(request.getStatus());
    }

    /**
     * Initializes the mapper instance.
     *
     * @return a singleton instance of the mapper
     */
    static UpdateServiceRequestToServiceEntityMapper initialize() {
        return Mappers.getMapper(UpdateServiceRequestToServiceEntityMapper.class);
    }

}

