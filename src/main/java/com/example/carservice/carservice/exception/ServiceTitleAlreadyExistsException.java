package com.example.carservice.carservice.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ServiceTitleAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3638116757704826505L;

    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    private static final String DEFAULT_MESSAGE = """
            A service with the specified title already exists!
            """;

    public ServiceTitleAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    public ServiceTitleAlreadyExistsException(final String title) {
        super(DEFAULT_MESSAGE + " Title: " + title);
    }
}

