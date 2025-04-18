package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a car with the same license plate already exists.
 * Typically used to return a 409 CONFLICT response.
 */
public class LicensePlateAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2162571158132920181L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            A car with this license plate already exists!
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public LicensePlateAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception including the conflicting license plate.
     *
     * @param plate the license plate that already exists
     */
    public LicensePlateAlreadyExistsException(final String plate) {
        super(DEFAULT_MESSAGE + " Plate: " + plate);
    }

}

