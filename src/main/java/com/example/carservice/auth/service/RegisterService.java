package com.example.carservice.auth.service;

import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.request.RegisterRequest;

public interface RegisterService {

    User registerUser(final RegisterRequest registerRequest);
}
