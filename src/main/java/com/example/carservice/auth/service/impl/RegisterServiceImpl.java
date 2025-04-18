package com.example.carservice.auth.service.impl;

import com.example.carservice.auth.exception.UserAlreadyExistException;
import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.request.RegisterRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.mapper.RegisterRequestToUserEntityMapper;
import com.example.carservice.auth.model.mapper.UserEntityToUserMapper;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.auth.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling user registration operations.
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;

    private final RegisterRequestToUserEntityMapper registerRequestToUserEntityMapper =
            RegisterRequestToUserEntityMapper.initialize();

    private final UserEntityToUserMapper userEntityToUserMapper = UserEntityToUserMapper.initialize();

    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registerRequest the request containing user registration details
     * @return the newly registered {@link User}
     */
    @Override
    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsUserEntityByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistException("The email is already used for another user : " + registerRequest.getEmail());
        }

        final UserEntity userEntityToBeSaved = registerRequestToUserEntityMapper.mapForSaving(registerRequest);

        userEntityToBeSaved.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        final UserEntity savedUserEntity = userRepository.save(userEntityToBeSaved);

        return userEntityToUserMapper.map(savedUserEntity);

    }

}
