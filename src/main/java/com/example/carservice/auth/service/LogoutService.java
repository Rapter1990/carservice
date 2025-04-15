package com.example.carservice.auth.service;

import com.example.carservice.auth.model.dto.request.TokenInvalidateRequest;

public interface LogoutService {

    void logout(final TokenInvalidateRequest tokenInvalidateRequest);

}
