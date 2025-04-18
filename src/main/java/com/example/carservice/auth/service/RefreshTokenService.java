package com.example.carservice.auth.service;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.TokenRefreshRequest;

/**
 * Service interface for refreshing JWT access tokens using a valid refresh token.
 */
public interface RefreshTokenService {

    /**
     * Generates a new access token using the provided refresh token.
     *
     * @param tokenRefreshRequest the request containing the refresh token
     * @return a new {@link Token} containing the refreshed access and refresh tokens
     */
    Token refreshToken(final TokenRefreshRequest tokenRefreshRequest);

}
