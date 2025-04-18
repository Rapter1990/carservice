package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a car with the specified ID cannot be found.
 * Typically used to return a 404 NOT FOUND response.
 */
public class CarNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6022779915715111661L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Car not found with the specified ID!
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public CarNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception with the car ID included in the message.
     *
     * @param carId the ID of the missing car
     */
    public CarNotFoundException(final String carId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId);
    }

}

