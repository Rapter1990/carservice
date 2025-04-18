package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a user's status is invalid for the requested operation.
 */
public class UserStatusNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6425123420333404947L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            User status is not valid!
            """;

    /**
     * Constructs a {@code UserStatusNotValidException} with a default message.
     */
    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserStatusNotValidException} with additional context.
     *
     * @param message further explanation of the invalid status
     */
    public UserStatusNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
