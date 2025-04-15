package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.carservice.exception.LicensePlateAlreadyExistsException;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.mapper.car.CarEntityToCarMapper;
import com.example.carservice.carservice.model.mapper.car.CreateCarRequestToCarEntityMapper;
import com.example.carservice.carservice.repository.CarRepository;
import com.example.carservice.carservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    private final UserIdentity userIdentity;

    private final CreateCarRequestToCarEntityMapper createCarRequestToCarEntityMapper = CreateCarRequestToCarEntityMapper.initialize();
    private final CarEntityToCarMapper carEntityToCarMapper = CarEntityToCarMapper.initialize();

    @Override
    @Transactional
    public Car assignCarToUser(CreateCarRequest createCarRequest) {

        this.checkCurrentUser(createCarRequest.getUserId());

        final UserEntity user = userRepository.findById(createCarRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + createCarRequest.getUserId()));

        if (carRepository.existsByLicensePlate(createCarRequest.getLicensePlate())) {
            throw new LicensePlateAlreadyExistsException(createCarRequest.getLicensePlate());
        }

        final CarEntity carEntity = createCarRequestToCarEntityMapper.mapForSaving(createCarRequest, user);

        final CarEntity savedCar = carRepository.save(carEntity);

        return carEntityToCarMapper.mapFromEntity(savedCar);

    }

    private void checkCurrentUser(String userId) {
        // Get the current user's ID from UserIdentity.
        String currentUserId = userIdentity.getUserId();
        if (!userId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to create a leave request for another user.");
        }
    }

}
