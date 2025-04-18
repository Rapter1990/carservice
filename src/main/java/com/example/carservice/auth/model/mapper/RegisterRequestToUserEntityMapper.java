package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.dto.request.RegisterRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting {@link RegisterRequest} DTOs to {@link UserEntity} entities.
 * Provides custom mapping logic for persisting a new user.
 */
@Mapper
public interface RegisterRequestToUserEntityMapper extends BaseMapper<RegisterRequest, UserEntity> {

    /**
     * Maps a {@link RegisterRequest} to a {@link UserEntity} for saving in the database.
     *
     * @param registerRequest the request object containing user registration details
     * @return a mapped {@link UserEntity} instance
     */
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

    /**
     * Initializes the mapper instance using MapStruct's generated implementation.
     *
     * @return the initialized {@link RegisterRequestToUserEntityMapper} instance
     */
    static RegisterRequestToUserEntityMapper initialize() {
        return Mappers.getMapper(RegisterRequestToUserEntityMapper.class);
    }

}
