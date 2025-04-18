package com.example.carservice.carservice.service.impl;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.model.UserIdentity;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.carservice.exception.CarNotFoundException;
import com.example.carservice.carservice.exception.CarStatusNotValidException;
import com.example.carservice.carservice.exception.LicensePlateAlreadyExistsException;
import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.carservice.model.entity.CarEntity;
import com.example.carservice.carservice.model.enums.CarStatus;
import com.example.carservice.carservice.model.mapper.car.CarEntityToCarMapper;
import com.example.carservice.carservice.model.mapper.car.CreateCarRequestToCarEntityMapper;
import com.example.carservice.carservice.model.mapper.car.ListCarEntityToListCarMapper;
import com.example.carservice.carservice.model.mapper.car.UpdateCarRequestToCarEntityMapper;
import com.example.carservice.carservice.repository.CarRepository;
import com.example.carservice.carservice.service.CarService;
import com.example.carservice.carservice.utils.UserPermissionUtils;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing car operations such as creation, assignment, retrieval, update, and deletion.
 */
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    private final UserIdentity userIdentity;

    private final CreateCarRequestToCarEntityMapper createCarRequestToCarEntityMapper = CreateCarRequestToCarEntityMapper.initialize();
    private final CarEntityToCarMapper carEntityToCarMapper = CarEntityToCarMapper.initialize();

    private final ListCarEntityToListCarMapper listCarEntityToListCarMapper = ListCarEntityToListCarMapper.initialize();

    private final UpdateCarRequestToCarEntityMapper updateCarRequestToCarEntityMapper = UpdateCarRequestToCarEntityMapper.initialize();

    /**
     * Creates a new car and assigns it to the specified user.
     *
     * @param createCarRequest the request containing car and user data
     * @return the newly created {@link Car}
     */
    @Override
    @Transactional
    public Car assignCarToUser(CreateCarRequest createCarRequest) {

        UserPermissionUtils.checkAccessPermission(userIdentity, createCarRequest.getUserId());

        final UserEntity user = userRepository.findById(createCarRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + createCarRequest.getUserId()));

        if (carRepository.existsByLicensePlate(createCarRequest.getLicensePlate())) {
            throw new LicensePlateAlreadyExistsException(createCarRequest.getLicensePlate());
        }

        final CarEntity carEntity = createCarRequestToCarEntityMapper.mapForSaving(createCarRequest, user);

        final CarEntity savedCar = carRepository.save(carEntity);

        return carEntityToCarMapper.mapFromEntity(savedCar);

    }

    /**
     * Retrieves car details by its unique identifier.
     *
     * @param carId the ID of the car
     * @return the corresponding {@link Car} object
     */
    @Override
    @Transactional(readOnly = true)
    public Car getCarById(String carId) {

        final CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        UserPermissionUtils.checkAccessPermission(userIdentity, carEntity.getUser().getId());

        return carEntityToCarMapper.mapFromEntity(carEntity);

    }

    /**
     * Retrieves a paginated list of all cars in the system.
     *
     * @param pagingRequest the pagination and sorting parameters
     * @return a paginated list of {@link Car} objects
     */
    @Override
    @Transactional(readOnly = true)
    public CustomPage<Car> getAllCars(CustomPagingRequest pagingRequest) {

        final Page<CarEntity> page = carRepository.findAll(pagingRequest.toPageable());

        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());

        return CustomPage.of(cars, page);

    }

    /**
     * Retrieves a paginated list of cars assigned to a specific user.
     *
     * @param userId the ID of the user
     * @param pagingRequest the pagination and sorting parameters
     * @return a paginated list of the user's cars
     */
    @Override
    @Transactional(readOnly = true)
    public CustomPage<Car> getAllCarsByUser(String userId, CustomPagingRequest pagingRequest) {

        UserPermissionUtils.checkAccessPermission(userIdentity, userId);

        final Page<CarEntity> page = carRepository.findByUserIdAndStatus(userId, CarStatus.ACTIVE, pagingRequest.toPageable());

        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());

        return CustomPage.of(cars, page);

    }

    /**
     * Retrieves a paginated list of all cars filtered by status.
     *
     * @param pagingRequest the pagination and sorting parameters
     * @return a filtered paginated list of {@link Car} entries
     */
    @Override
    @Transactional(readOnly = true)
    public CustomPage<Car> getAllCarsByStatus(CustomPagingRequest pagingRequest) {

        final Page<CarEntity> page = carRepository.findByStatus(CarStatus.ACTIVE, pagingRequest.toPageable());

        final List<Car> cars = listCarEntityToListCarMapper.toCarList(page.getContent());

        return CustomPage.of(cars, page);
    }

    /**
     * Updates the details of a car.
     *
     * @param carId the ID of the car to update
     * @param request the update request payload
     * @return the updated {@link Car}
     */
    @Override
    @Transactional
    public Car updateCar(String carId, UpdateCarRequest request) {

        // Fetch existing car
        CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        if (carEntity.getStatus() != CarStatus.ACTIVE) {
            throw new CarStatusNotValidException("Only ACTIVE cars can be updated.");
        }

        // Validate permission
        UserPermissionUtils.checkAccessPermission(userIdentity, request.getUserId());

        // Check license plate uniqueness if changed
        if (!carEntity.getLicensePlate().equals(request.getLicensePlate()) &&
                carRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new LicensePlateAlreadyExistsException(request.getLicensePlate());
        }

        UserEntity targetUser = carEntity.getUser();

        if (!targetUser.getId().equals(request.getUserId())) {
            targetUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getUserId()));
        }

        updateCarRequestToCarEntityMapper.updateEntity(request, carEntity, targetUser);

        CarEntity updatedCar = carRepository.save(carEntity);

        return carEntityToCarMapper.mapFromEntity(updatedCar);

    }

    /**
     * Deletes a car by its unique identifier.
     *
     * @param carId the ID of the car to delete
     */
    @Override
    @Transactional
    public void deleteCar(String carId) {
        CarEntity carEntity = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        UserPermissionUtils.checkAccessPermission(userIdentity, carEntity.getUser().getId());

        if (carEntity.getStatus() == CarStatus.DELETED) {
            return; // Already soft-deleted, optionally throw exception or just ignore
        }

        carEntity.setStatus(CarStatus.DELETED);
        carRepository.save(carEntity);
    }

}
