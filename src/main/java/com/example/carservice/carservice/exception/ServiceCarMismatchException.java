package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a service does not belong to the specified car.
 * Typically used to return a 400 BAD REQUEST response.
 */
public class ServiceCarMismatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7533608503266357760L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            The service does not belong to the specified car.
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public ServiceCarMismatchException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception including the mismatched car and service IDs.
     *
     * @param carId     the ID of the car
     * @param serviceId the ID of the service
     */
    public ServiceCarMismatchException(final String carId, final String serviceId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId + ", Service ID: " + serviceId);
    }

}
