package com.example.carservice.auth.security;

import com.example.carservice.common.model.CustomError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} to handle unauthorized access attempts.
 * This component is triggered when an unauthenticated user attempts to access a secured resource.
 * It returns a structured JSON response with {@code 401 Unauthorized} status using {@link CustomError}.
 * Registered as a Spring {@link Component} and used in conjunction with Spring Security.
 *
 * @see org.springframework.security.web.AuthenticationEntryPoint
 * @see CustomError
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * Handles authentication failures by returning a JSON-formatted {@link CustomError} response.
     *
     * @param httpServletRequest  the current HTTP request
     * @param httpServletResponse the HTTP response to be modified
     * @param authenticationException the exception indicating authentication failure
     * @throws IOException if writing to the response fails
     */
    @Override
    public void commence(final HttpServletRequest httpServletRequest,
                         final HttpServletResponse httpServletResponse,
                         final AuthenticationException authenticationException) throws IOException {

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

        final CustomError customError = CustomError.builder()
                .header(CustomError.Header.AUTH_ERROR.getName())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .isSuccess(false)
                .build();

        final String responseBody = OBJECT_MAPPER
                .writer(DateFormat.getDateInstance())
                .writeValueAsString(customError);

        httpServletResponse.getOutputStream()
                .write(responseBody.getBytes());

    }

}
