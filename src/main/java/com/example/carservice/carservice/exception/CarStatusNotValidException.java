package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class CarStatusNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5074596708312486239L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Car status is not valid for the requested operation!
            """;

    public CarStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public CarStatusNotValidException(final String carId) {
        super(DEFAULT_MESSAGE + " Car ID: " + carId);
    }

}

