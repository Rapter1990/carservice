package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class CarNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6022779915715111661L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Car not found with the specified ID!
            """;

    public CarNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public CarNotFoundException(final String carId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId);
    }
}

