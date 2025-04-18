package com.example.carservice.auth.service;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.LoginRequest;

/**
 * Service interface for authenticating users and issuing JWT tokens.
 */
public interface LoginService {

    /**
     * Authenticates the user based on the login request and issues a new {@link Token}.
     *
     * @param loginRequest the request containing login credentials
     * @return a {@link Token} if authentication is successful
     */
    Token login(final LoginRequest loginRequest);

}
