package com.example.carservice.auth.service.impl;

import com.example.carservice.auth.exception.PasswordNotValidException;
import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.LoginRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.auth.service.LoginService;
import com.example.carservice.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public Token login(LoginRequest loginRequest) {

        final UserEntity userEntityFromDB = userRepository
                .findUserEntityByEmail(loginRequest.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException(loginRequest.getEmail())
                );

        if (Boolean.FALSE.equals(passwordEncoder.matches(
                loginRequest.getPassword(), userEntityFromDB.getPassword()))) {
            throw new PasswordNotValidException();
        }

        return tokenService.generateToken(userEntityFromDB.getClaims());
    }

}
