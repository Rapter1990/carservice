package com.example.carservice.auth.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Exception thrown when an attempt is made to use a JWT token that has already been invalidated.
 */
public class TokenAlreadyInvalidatedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 362642291999732144L;

    public static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    private static final String DEFAULT_MESSAGE = """
            Token is already invalidated!
            """;

    /**
     * Constructs a {@code TokenAlreadyInvalidatedException} with a default message.
     */
    public TokenAlreadyInvalidatedException() {
        super(DEFAULT_MESSAGE);
    }

    /**
     * Constructs a {@code TokenAlreadyInvalidatedException} including the token ID.
     *
     * @param tokenId the ID of the invalidated token
     */
    public TokenAlreadyInvalidatedException(final String tokenId) {
        super(DEFAULT_MESSAGE + " TokenID = " + tokenId);
    }

}
