package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when attempting to register a user that already exists.
 */
public class UserAlreadyExistException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6022982105123187545L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            User already exist!
            """;

    /**
     * Constructs a {@code UserAlreadyExistException} with a default message.
     */
    public UserAlreadyExistException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code UserAlreadyExistException} with additional context.
     *
     * @param message additional detail about the conflict
     */
    public UserAlreadyExistException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
