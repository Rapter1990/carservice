package com.example.carservice.auth.service;

import com.example.carservice.auth.model.dto.request.TokenInvalidateRequest;

/**
 * Service interface for handling user logout operations.
 */
public interface LogoutService {

    /**
     * Invalidates the user's tokens during logout.
     *
     * @param tokenInvalidateRequest the request containing token IDs to invalidate
     */
    void logout(final TokenInvalidateRequest tokenInvalidateRequest);

}
