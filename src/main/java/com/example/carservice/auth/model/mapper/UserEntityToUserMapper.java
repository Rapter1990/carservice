package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting {@link UserEntity} entities to {@link User} domain models.
 */
@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    /**
     * Maps a {@link UserEntity} to a {@link User}.
     *
     * @param source the source {@link UserEntity}
     * @return the mapped {@link User}
     */
    @Override
    User map(UserEntity source);

    /**
     * Initializes the mapper instance using MapStruct's generated implementation.
     *
     * @return the initialized {@link UserEntityToUserMapper} instance
     */
    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}
