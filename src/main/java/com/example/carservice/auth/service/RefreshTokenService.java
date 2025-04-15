package com.example.carservice.auth.service;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.TokenRefreshRequest;

public interface RefreshTokenService {

    Token refreshToken(final TokenRefreshRequest tokenRefreshRequest);

}
