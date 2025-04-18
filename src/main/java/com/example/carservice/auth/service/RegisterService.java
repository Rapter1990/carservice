package com.example.carservice.auth.service;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.request.RegisterRequest;

/**
 * Service interface for handling user registration operations.
 */
public interface RegisterService {

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registerRequest the request containing user registration details
     * @return the newly registered {@link User}
     */
    User registerUser(final RegisterRequest registerRequest);
}
