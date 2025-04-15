package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.dto.request.RegisterRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegisterRequestToUserEntityMapper extends BaseMapper<RegisterRequest, UserEntity> {

    @Named("mapForSaving")
    default UserEntity mapForSaving(RegisterRequest registerRequest) {
        return UserEntity.builder()
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .userType(registerRequest.getUserType())
                .build();
    }

    static RegisterRequestToUserEntityMapper initialize() {
        return Mappers.getMapper(RegisterRequestToUserEntityMapper.class);
    }

}
