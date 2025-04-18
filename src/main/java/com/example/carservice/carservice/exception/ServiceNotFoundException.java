package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when a service with the specified ID cannot be found.
 * Typically used to return a 404 NOT FOUND response.
 */
public class ServiceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5140159666641300463L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Service not found with the specified ID!
            """;

    /**
     * Constructs a new exception with a default message.
     */
    public ServiceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a new exception including the missing service ID.
     *
     * @param serviceId the ID of the missing service
     */
    public ServiceNotFoundException(final String serviceId) {
        super(DEFAULT_MESSAGE + " Service ID: " + serviceId);
    }

}

