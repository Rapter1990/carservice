package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceDtoToServiceResponseMapper extends BaseMapper<ServiceDto, ServiceResponse> {

    @Override
    ServiceResponse map(ServiceDto source);

    static ServiceDtoToServiceResponseMapper initialize() {
        return Mappers.getMapper(ServiceDtoToServiceResponseMapper.class);
    }
}

