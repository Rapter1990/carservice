package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a user cannot be found by their ID, email, or other identifiers.
 */
public class UserNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8652324803834661177L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            User not found!
            """;

    /**
     * Constructs a {@code UserNotFoundException} with a default message.
     */
    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserNotFoundException} with additional context.
     *
     * @param message detail about the failed lookup
     */
    public UserNotFoundException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
