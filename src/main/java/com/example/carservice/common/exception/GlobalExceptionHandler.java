package com.example.carservice.common.exception;

import com.example.carservice.auth.exception.*;
import com.example.carservice.carservice.exception.*;
import com.example.carservice.common.model.CustomError;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the Car Service application.
 * This class handles various exceptions thrown throughout the application and returns
 * standardized {@link CustomError} responses with appropriate HTTP status codes.
 *
 * <p>Handled exceptions include:</p>
 * <ul>
 *     <li>Validation errors</li>
 *     <li>Authentication and authorization issues</li>
 *     <li>Resource not found and conflict scenarios</li>
 *     <li>Domain-specific application exceptions</li>
 * </ul>
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles validation errors for request bodies annotated with {@code @Valid}.
     *
     * @param ex the thrown {@link MethodArgumentNotValidException}
     * @return a {@link ResponseEntity} containing a {@link CustomError} with detailed field-level validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {

        List<CustomError.CustomSubError> subErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(
                error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    subErrors.add(
                            CustomError.CustomSubError.builder()
                                    .field(fieldName)
                                    .message(message)
                                    .build()
                    );
                }
        );

        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(subErrors)
                .build();

        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handles validation errors for request parameters and path variables.
     *
     * @param ex the thrown {@link ConstraintViolationException}
     * @return a {@link ResponseEntity} containing a {@link CustomError} with constraint violation details
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handlePathVariableErrors(final ConstraintViolationException ex) {

        List<CustomError.CustomSubError> subErrors = new ArrayList<>();
        ex.getConstraintViolations()
                .forEach(constraintViolation ->
                        subErrors.add(
                                CustomError.CustomSubError.builder()
                                        .message(constraintViolation.getMessage())
                                        .field(StringUtils.substringAfterLast(constraintViolation.getPropertyPath().toString(), "."))
                                        .value(constraintViolation.getInvalidValue() != null ? constraintViolation.getInvalidValue().toString() : null)
                                        .type(constraintViolation.getInvalidValue().getClass().getSimpleName())
                                        .build()
                        )
                );

        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Constraint violation")
                .subErrors(subErrors)
                .build();

        return new ResponseEntity<>(customError, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handles uncaught {@link RuntimeException} instances.
     *
     * @param ex the thrown {@link RuntimeException}
     * @return a {@link ResponseEntity} containing a generic {@link CustomError}
     */
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleRuntimeException(final RuntimeException ex) {
        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles authorization errors triggered by Spring Security.
     *
     * @param ex the thrown {@link AccessDeniedException}
     * @return a {@link ResponseEntity} with {@code 403 Forbidden} and a {@link CustomError}
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDeniedException(final AccessDeniedException ex) {
        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles invalid password exception during authentication.
     *
     * @param ex the thrown {@link PasswordNotValidException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(PasswordNotValidException.class)
    protected ResponseEntity<CustomError> handlePasswordNotValidException(final PasswordNotValidException ex) {

        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles reuse of an already invalidated JWT token.
     *
     * @param ex the thrown {@link TokenAlreadyInvalidatedException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(TokenAlreadyInvalidatedException.class)
    protected ResponseEntity<CustomError> handleTokenAlreadyInvalidatedException(final TokenAlreadyInvalidatedException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles user registration when the user already exists.
     *
     * @param ex the thrown {@link UserAlreadyExistException}
     * @return a {@link ResponseEntity} with {@code 409 Conflict} and a {@link CustomError}
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    protected ResponseEntity<CustomError> handleUserAlreadyExistException(final UserAlreadyExistException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles lookup failures for a user by ID or other identifier.
     *
     * @param ex the thrown {@link UserNotFoundException}
     * @return a {@link ResponseEntity} with {@code 404 Not Found} and a {@link CustomError}
     */
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<CustomError> handleUserNotFoundException(final UserNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles invalid user status during login or actions.
     *
     * @param ex the thrown {@link UserStatusNotValidException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(UserStatusNotValidException.class)
    protected ResponseEntity<CustomError> handleUserStatusNotValidException(final UserStatusNotValidException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles unauthorized access attempts without proper authentication.
     *
     * @param ex the thrown {@link UnAuthorizeAttemptException}
     * @return a {@link ResponseEntity} with {@code 401 Unauthorized} and a {@link CustomError}
     */
    @ExceptionHandler(UnAuthorizeAttemptException.class)
    protected ResponseEntity<Object> handleUnAuthorizeAttempt(final UnAuthorizeAttemptException ex){

        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(customError, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles duplication of a car's license plate.
     *
     * @param ex the thrown {@link LicensePlateAlreadyExistsException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(LicensePlateAlreadyExistsException.class)
    protected ResponseEntity<Object> handleLicensePlateAlreadyExistsException(final LicensePlateAlreadyExistsException ex) {
        CustomError customError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(customError, LicensePlateAlreadyExistsException.STATUS);
    }

    /**
     * Handles cases where a car cannot be found.
     *
     * @param ex the thrown {@link CarNotFoundException}
     * @return a {@link ResponseEntity} with {@code 404 Not Found} and a {@link CustomError}
     */
    @ExceptionHandler(CarNotFoundException.class)
    protected ResponseEntity<CustomError> handleCarNotFoundException(final CarNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(error, CarNotFoundException.STATUS);
    }

    /**
     * Handles invalid car status during updates or service requests.
     *
     * @param ex the thrown {@link CarStatusNotValidException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(CarStatusNotValidException.class)
    protected ResponseEntity<CustomError> handleCarStatusNotValidException(final CarStatusNotValidException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(CarStatusNotValidException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(error, CarStatusNotValidException.STATUS);
    }

    /**
     * Handles cases where a service record cannot be found.
     *
     * @param ex the thrown {@link ServiceNotFoundException}
     * @return a {@link ResponseEntity} with {@code 404 Not Found} and a {@link CustomError}
     */
    @ExceptionHandler(ServiceNotFoundException.class)
    protected ResponseEntity<CustomError> handleServiceNotFoundException(final ServiceNotFoundException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(ServiceNotFoundException.STATUS)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(error, ServiceNotFoundException.STATUS);
    }

    /**
     * Handles mismatches between a car and the service attempting to reference it.
     *
     * @param ex the thrown {@link ServiceCarMismatchException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(ServiceCarMismatchException.class)
    protected ResponseEntity<CustomError> handleServiceCarMismatchException(final ServiceCarMismatchException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(ServiceCarMismatchException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(error, ServiceCarMismatchException.STATUS);
    }

    /**
     * Handles duplication of a service title.
     *
     * @param ex the thrown {@link ServiceTitleAlreadyExistsException}
     * @return a {@link ResponseEntity} with {@code 400 Bad Request} and a {@link CustomError}
     */
    @ExceptionHandler(ServiceTitleAlreadyExistsException.class)
    protected ResponseEntity<CustomError> handleServiceTitleAlreadyExistsException(final ServiceTitleAlreadyExistsException ex) {
        CustomError error = CustomError.builder()
                .httpStatus(ServiceTitleAlreadyExistsException.STATUS)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message(ex.getMessage())
                .isSuccess(false)
                .build();

        return new ResponseEntity<>(error, ServiceTitleAlreadyExistsException.STATUS);
    }

}
