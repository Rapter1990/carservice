package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.Service;
import com.example.carservice.carservice.model.dto.response.ServiceResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceToServiceResponseMapper extends BaseMapper<Service, ServiceResponse> {

    @Override
    ServiceResponse map(Service source);

    static ServiceToServiceResponseMapper initialize() {
        return Mappers.getMapper(ServiceToServiceResponseMapper.class);
    }
}

