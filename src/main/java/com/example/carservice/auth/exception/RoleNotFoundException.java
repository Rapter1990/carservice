package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a requested role cannot be found in the system.
 */
public class RoleNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2569721915183083303L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Role not found!
            """;

    /**
     * Constructs a {@code RoleNotFoundException} with a default message.
     */
    public RoleNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code RoleNotFoundException} with additional details.
     *
     * @param message additional description for debugging
     */
    public RoleNotFoundException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
