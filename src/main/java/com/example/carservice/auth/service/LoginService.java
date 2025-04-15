package com.example.carservice.auth.service;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.LoginRequest;

public interface LoginService {

    Token login(final LoginRequest loginRequest);
}
