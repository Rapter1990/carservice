package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a provided password is not valid during authentication or validation.
 */
public class PasswordNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7740710932401442261L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Password is not valid!
            """;

    /**
     * Constructs a {@code PasswordNotValidException} with a default message.
     */
    public PasswordNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code PasswordNotValidException} with additional context message.
     *
     * @param message additional information about the password failure
     */
    public PasswordNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
