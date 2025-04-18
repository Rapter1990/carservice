package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a car's status is not valid for the attempted operation.
 * Typically used to return a 400 BAD REQUEST response.
 */
public class CarStatusNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5074596708312486239L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Car status is not valid for the requested operation!
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public CarStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception with the car ID included in the message.
     *
     * @param carId the ID of the car with invalid status
     */
    public CarStatusNotValidException(final String carId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId);
    }

}

