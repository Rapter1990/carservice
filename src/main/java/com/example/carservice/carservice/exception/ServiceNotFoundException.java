package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ServiceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5140159666641300463L;

    public static final HttpStatus STATUS = HttpStatus.NOT_FOUND;

    private static final String DEFAULT_MESSAGE = """
            Service not found with the specified ID!
            """;

    public ServiceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ServiceNotFoundException(final String serviceId) {
        super(DEFAULT_MESSAGE + " Service ID: " + serviceId);
    }

}

