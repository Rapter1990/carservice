package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.response.UserResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserToUserResponseMapper extends BaseMapper<User, UserResponse> {

    @Override
    UserResponse map(User source);

    static UserToUserResponseMapper initialize() {
        return Mappers.getMapper(UserToUserResponseMapper.class);
    }
}

