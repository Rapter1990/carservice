package com.example.carservice.auth.model.mapper;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.response.TokenResponse;
import com.example.carservice.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper for converting a {@link Token} domain model to a {@link TokenResponse} DTO.
 */
@Mapper
public interface TokenToTokenResponseMapper extends BaseMapper<Token, TokenResponse> {

    /**
     * Maps a {@link Token} to a {@link TokenResponse}.
     *
     * @param source the source {@link Token}
     * @return the mapped {@link TokenResponse}
     */
    @Override
    TokenResponse map(Token source);

    /**
     * Initializes the mapper instance using MapStruct's generated implementation.
     *
     * @return the initialized {@link TokenToTokenResponseMapper} instance
     */
    static TokenToTokenResponseMapper initialize() {
        return Mappers.getMapper(TokenToTokenResponseMapper.class);
    }

}
