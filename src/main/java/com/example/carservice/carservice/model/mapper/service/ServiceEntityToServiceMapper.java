package com.example.carservice.carservice.model.mapper.service;

import com.example.carservice.carservice.model.Service;
import com.example.carservice.carservice.model.entity.ServiceEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ServiceEntityToServiceMapper extends BaseMapper<ServiceEntity, Service> {

    @Override
    Service map(ServiceEntity source);

    static ServiceEntityToServiceMapper initialize() {
        return Mappers.getMapper(ServiceEntityToServiceMapper.class);
    }
}
