package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting {@link ServiceEntity} to {@link ServiceDto}.
 * Used when fetching service data from the database and preparing it for business logic.
 */
@Mapper
public interface ServiceEntityToServiceDtoMapper extends BaseMapper<ServiceEntity, ServiceDto> {

    /**
     * Maps a {@link ServiceEntity} to a {@link ServiceDto}.
     *
     * @param source the service entity
     * @return the mapped service DTO
     */
    @Override
    ServiceDto map(ServiceEntity source);

    /**
     * Initializes the mapper instance.
     *
     * @return a singleton instance of the mapper
     */
    static ServiceEntityToServiceDtoMapper initialize() {
        return Mappers.getMapper(ServiceEntityToServiceDtoMapper.class);
    }

}
