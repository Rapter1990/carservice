package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting {@link ServiceDto} to {@link ServiceResponse}.
 * Used to prepare service data for API responses.
 */
@Mapper
public interface ServiceDtoToServiceResponseMapper extends BaseMapper<ServiceDto, ServiceResponse> {

    /**
     * Maps a {@link ServiceDto} to a {@link ServiceResponse}.
     *
     * @param source the service DTO
     * @return the mapped service response
     */
    @Override
    ServiceResponse map(ServiceDto source);

    /**
     * Initializes the mapper instance.
     *
     * @return a singleton instance of the mapper
     */
    static ServiceDtoToServiceResponseMapper initialize() {
        return Mappers.getMapper(ServiceDtoToServiceResponseMapper.class);
    }

}

