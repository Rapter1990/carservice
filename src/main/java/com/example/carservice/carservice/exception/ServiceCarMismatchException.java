package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ServiceCarMismatchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7533608503266357760L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            The service does not belong to the specified car.
            """;

    public ServiceCarMismatchException() {
        super(DEFAULT_MESSAGE);
    }

    public ServiceCarMismatchException(final String carId, final String serviceId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId + ", Service ID: " + serviceId);
    }

}
