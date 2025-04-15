package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class LicensePlateAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2162571158132920181L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            A car with this license plate already exists!
            """;

    public LicensePlateAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public LicensePlateAlreadyExistsException(final String plate) {
        super(DEFAULT_MESSAGE + " Plate: " + plate);
    }
}

