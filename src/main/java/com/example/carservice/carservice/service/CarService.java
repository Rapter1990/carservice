package com.example.carservice.carservice.service;

import com.example.carservice.carservice.model.Car;
import com.example.carservice.carservice.model.dto.request.car.CreateCarRequest;
import com.example.carservice.carservice.model.dto.request.car.UpdateCarRequest;
import com.example.carservice.common.model.CustomPage;
import com.example.carservice.common.model.dto.request.CustomPagingRequest;

/**
 * Service interface for managing car operations such as creation, assignment, retrieval, update, and deletion.
 */
public interface CarService {

    /**
     * Creates a new car and assigns it to the specified user.
     *
     * @param createCarRequest the request containing car and user data
     * @return the newly created {@link Car}
     */
    Car assignCarToUser(final CreateCarRequest createCarRequest);

    /**
     * Retrieves car details by its unique identifier.
     *
     * @param carId the ID of the car
     * @return the corresponding {@link Car} object
     */
    Car getCarById(final String carId);

    /**
     * Retrieves a paginated list of all cars in the system.
     *
     * @param pagingRequest the pagination and sorting parameters
     * @return a paginated list of {@link Car} objects
     */
    CustomPage<Car> getAllCars(final CustomPagingRequest pagingRequest);

    /**
     * Retrieves a paginated list of cars assigned to a specific user.
     *
     * @param userId the ID of the user
     * @param pagingRequest the pagination and sorting parameters
     * @return a paginated list of the user's cars
     */
    CustomPage<Car> getAllCarsByUser(final String userId, final CustomPagingRequest pagingRequest);

    /**
     * Retrieves a paginated list of all cars filtered by status.
     *
     * @param pagingRequest the pagination and sorting parameters
     * @return a filtered paginated list of {@link Car} entries
     */
    CustomPage<Car> getAllCarsByStatus(CustomPagingRequest pagingRequest);

    /**
     * Updates the details of a car.
     *
     * @param carId the ID of the car to update
     * @param request the update request payload
     * @return the updated {@link Car}
     */
    Car updateCar(String carId, UpdateCarRequest request);

    /**
     * Deletes a car by its unique identifier.
     *
     * @param carId the ID of the car to delete
     */
    void deleteCar(String carId);

}
