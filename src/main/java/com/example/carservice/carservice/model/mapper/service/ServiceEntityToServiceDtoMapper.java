package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.ServiceDto;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceEntityToServiceDtoMapper extends BaseMapper<ServiceEntity, ServiceDto> {

    @Override
    ServiceDto map(ServiceEntity source);

    static ServiceEntityToServiceDtoMapper initialize() {
        return Mappers.getMapper(ServiceEntityToServiceDtoMapper.class);
    }

}
