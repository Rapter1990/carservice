package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a service with the same title already exists.
 * Typically used to return a 409 CONFLICT response.
 */
public class ServiceTitleAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3638116757704826505L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            A service with the specified title already exists!
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public ServiceTitleAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception including the duplicate service title.
     *
     * @param title the title that already exists
     */
    public ServiceTitleAlreadyExistsException(final String title) {
        super(DEFAULT_MESSAGE + " Title: " + title);
    }

}

