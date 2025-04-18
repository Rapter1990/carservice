package com.example.carservice.auth.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.exception.UserStatusNotValidException;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.TokenRefreshRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.auth.service.RefreshTokenService;
import com.example.carservice.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for refreshing JWT access tokens using a valid refresh token.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    /**
     * Generates a new access token using the provided refresh token.
     *
     * @param tokenRefreshRequest the request containing the refresh token
     * @return a new {@link Token} containing the refreshed access and refresh tokens
     */
    @Override
    public Token refreshToken(TokenRefreshRequest tokenRefreshRequest) {

        tokenService.verifyAndValidate(tokenRefreshRequest.getRefreshToken());

        final String adminId = tokenService
                .getPayload(tokenRefreshRequest.getRefreshToken())
                .get(TokenClaims.USER_ID.getValue())
                .toString();

        final UserEntity userEntityFromDB = userRepository
                .findById(adminId)
                .orElseThrow(UserNotFoundException::new);

        this.validateAdminStatus(userEntityFromDB);

        return tokenService.generateToken(
                userEntityFromDB.getClaims(),
                tokenRefreshRequest.getRefreshToken()
        );
    }

    private void validateAdminStatus(final UserEntity userEntity) {
        if (!(UserStatus.ACTIVE.equals(userEntity.getUserStatus()))) {
            throw new UserStatusNotValidException("UserStatus = " + userEntity.getUserStatus());
        }
    }

}
