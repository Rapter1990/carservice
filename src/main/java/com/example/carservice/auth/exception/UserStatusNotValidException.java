package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class UserStatusNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6425123420333404947L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            User status is not valid!
            """;

    public UserStatusNotValidException() {
        super(DEFAULT_MESSAGE);
    }

    public UserStatusNotValidException(final String message) {
        super(DEFAULT_MESSAGE + " " + message);
    }

}
