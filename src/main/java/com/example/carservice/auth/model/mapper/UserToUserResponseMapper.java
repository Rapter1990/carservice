package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.response.UserResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting {@link User} domain models to {@link UserResponse} DTOs.
 */
@Mapper
public interface UserToUserResponseMapper extends BaseMapper<User, UserResponse> {

    /**
     * Maps a {@link User} to a {@link UserResponse}.
     *
     * @param source the source {@link User}
     * @return the mapped {@link UserResponse}
     */
    @Override
    UserResponse map(User source);

    /**
     * Initializes the mapper instance using MapStruct's generated implementation.
     *
     * @return the initialized {@link UserToUserResponseMapper} instance
     */
    static UserToUserResponseMapper initialize() {
        return Mappers.getMapper(UserToUserResponseMapper.class);
    }

}

