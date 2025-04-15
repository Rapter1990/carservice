package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserEntityToUserMapper extends BaseMapper<UserEntity, User> {

    @Override
    User map(UserEntity source);

    static UserEntityToUserMapper initialize() {
        return Mappers.getMapper(UserEntityToUserMapper.class);
    }

}
