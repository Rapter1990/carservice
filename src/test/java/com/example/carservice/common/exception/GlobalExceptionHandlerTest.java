package com.example.carservice.common.exception;

import com.example.carservice.auth.exception.*;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.*;
import com.example.carservice.common.model.CustomError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest extends AbstractBaseServiceTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void givenMethodArgumentNotValidException_whenHandleMethodArgumentNotValid_thenRespondWithBadRequest() {

        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");
        List<ObjectError> objectErrors = Collections.singletonList(fieldError);

        when(bindingResult.getAllErrors()).thenReturn(objectErrors);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Validation failed")
                .subErrors(Collections.singletonList(
                        CustomError.CustomSubError.builder()
                                .field("fieldName")
                                .message("error message")
                                .build()))
                .build();

        // When
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleMethodArgumentNotValid(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        CustomError actualError = (CustomError) responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenConstraintViolationException_whenHandlePathVariableErrors_thenRespondWithBadRequest() {

        // Given
        ConstraintViolation<String> mockViolation = mock(ConstraintViolation.class);
        Path mockPath = mock(Path.class);
        Set<ConstraintViolation<?>> violations = Set.of(mockViolation);
        ConstraintViolationException mockException = new ConstraintViolationException(violations);

        CustomError.CustomSubError subError = CustomError.CustomSubError.builder()
                .message("must not be null")
                .field("")
                .value("invalid value")
                .type("String") // Default to String if getRootBeanClass() is null
                .build();

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Constraint violation")
                .subErrors(Collections.singletonList(subError))
                .build();

        // When
        when(mockViolation.getMessage()).thenReturn("must not be null");
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("field");
        when(mockViolation.getInvalidValue()).thenReturn("invalid value");
        when(mockViolation.getRootBeanClass()).thenReturn(String.class); // Ensure this does not return null

        // Then
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handlePathVariableErrors(mockException);

        CustomError actualError = (CustomError) responseEntity.getBody();

        // Verify
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenRuntimeException_whenHandleRuntimeException_thenRespondWithNotFound() {

        // Given
        RuntimeException ex = new RuntimeException("Runtime exception message");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.API_ERROR.getName())
                .message("Runtime exception message")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleRuntimeException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        CustomError actualError = (CustomError) responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenAccessDeniedException_whenHandleAccessDeniedException_thenRespondWithForbidden() {

        // Given
        AccessDeniedException ex = new AccessDeniedException("Access denied message");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message("Access denied message")
                .build();

        // When
        ResponseEntity<?> responseEntity = globalExceptionHandler.handleAccessDeniedException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        CustomError actualError = (CustomError) responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenPasswordNotValidException_whenHandlePasswordNotValidException_thenRespondWithBadRequest() {

        // Given
        PasswordNotValidException ex = new PasswordNotValidException("Password not valid message");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Password is not valid!\n Password not valid message")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handlePasswordNotValidException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenTokenAlreadyInvalidatedException_whenHandleTokenAlreadyInvalidatedException_thenRespondWithBadRequest() {

        // Given
        TokenAlreadyInvalidatedException ex = new TokenAlreadyInvalidatedException("Token already invalidated");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Token is already invalidated!\n TokenID = Token already invalidated")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleTokenAlreadyInvalidatedException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenUserAlreadyExistException_whenHandleUserAlreadyExistException_thenRespondWithConflict() {

        // Given
        UserAlreadyExistException ex = new UserAlreadyExistException("User already exists");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message("User already exist!\n User already exists")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleUserAlreadyExistException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenUserNotFoundException_whenHandleUserNotFoundException_thenRespondWithNotFound() {

        // Given
        UserNotFoundException ex = new UserNotFoundException("User not found");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message("User not found!\n User not found")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleUserNotFoundException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenUserStatusNotValidException_whenHandleUserStatusNotValidException_thenRespondWithBadRequest() {

        // Given
        UserStatusNotValidException ex = new UserStatusNotValidException("User status not valid");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("User status is not valid!\n User status not valid")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleUserStatusNotValidException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenUnAuthorizeAttemptException_whenHandleUnAuthorizeAttempt_thenRespondWithUnauthorized() {

        // Given
        UnAuthorizeAttemptException ex = new UnAuthorizeAttemptException();

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .header(CustomError.Header.AUTH_ERROR.getName())
                .message("You do not have permission to create a to-do item.\n")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleUnAuthorizeAttempt(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        CustomError actualError = (CustomError) responseEntity.getBody(); // Cast response body
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenLicensePlateAlreadyExistsException_whenHandleLicensePlateAlreadyExistsException_thenRespondWithConflict() {

        // Given
        LicensePlateAlreadyExistsException ex = new LicensePlateAlreadyExistsException("34 ABC 123");

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.ALREADY_EXIST.getName())
                .message("A car with this license plate already exists!\n Plate: 34 ABC 123")
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<Object> responseEntity = globalExceptionHandler.handleLicensePlateAlreadyExistsException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        CustomError actualError = (CustomError) responseEntity.getBody(); // Cast response body
        checkCustomError(expectedError, actualError);
    }

    @Test
    void givenCarNotFoundException_whenHandleCarNotFoundException_thenRespondWithNotFound() {

        // Given
        String carId = UUID.randomUUID().toString();
        CarNotFoundException ex = new CarNotFoundException(carId);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message("Car not found with the specified ID!\n Car ID: " + carId)
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleCarNotFoundException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenCarStatusNotValidException_whenHandleCarStatusNotValidException_thenRespondWithBadRequest() {

        // Given
        String carId = UUID.randomUUID().toString();
        CarStatusNotValidException ex = new CarStatusNotValidException(carId);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("Car status is not valid for the requested operation!\n Car ID: " + carId)
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> responseEntity = globalExceptionHandler.handleCarStatusNotValidException(ex);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        CustomError actualError = responseEntity.getBody();
        checkCustomError(expectedError, actualError);

    }

    @Test
    void givenServiceNotFoundException_whenHandleServiceNotFoundException_thenRespondWithNotFound() {
        // Given
        String serviceId = UUID.randomUUID().toString();
        ServiceNotFoundException ex = new ServiceNotFoundException(serviceId);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .header(CustomError.Header.NOT_FOUND.getName())
                .message("Service not found with the specified ID!\n Service ID: " + serviceId)
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> response = globalExceptionHandler.handleServiceNotFoundException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        checkCustomError(expectedError, response.getBody());
    }

    @Test
    void givenServiceCarMismatchException_whenHandleServiceCarMismatchException_thenRespondWithBadRequest() {

        // Given
        String carId = UUID.randomUUID().toString();
        String serviceId = UUID.randomUUID().toString();
        ServiceCarMismatchException ex = new ServiceCarMismatchException(carId, serviceId);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("The service does not belong to the specified car.\n Car ID: " + carId + ", Service ID: " + serviceId)
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> response = globalExceptionHandler.handleServiceCarMismatchException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        checkCustomError(expectedError, response.getBody());

    }

    @Test
    void givenServiceTitleAlreadyExistsException_whenHandleServiceTitleAlreadyExistsException_thenRespondWithConflict() {

        // Given
        String title = "Brake Inspection";
        ServiceTitleAlreadyExistsException ex = new ServiceTitleAlreadyExistsException(title);

        CustomError expectedError = CustomError.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .header(CustomError.Header.VALIDATION_ERROR.getName())
                .message("A service with the specified title already exists!\n Title: " + title)
                .isSuccess(false)
                .build();

        // When
        ResponseEntity<CustomError> response = globalExceptionHandler.handleServiceTitleAlreadyExistsException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        checkCustomError(expectedError, response.getBody());

    }

    private void checkCustomError(CustomError expectedError, CustomError actualError) {

        assertThat(actualError).isNotNull();
        assertThat(actualError.getTime()).isNotNull();
        assertThat(actualError.getHeader()).isEqualTo(expectedError.getHeader());
        assertThat(actualError.getIsSuccess()).isEqualTo(expectedError.getIsSuccess());

        if (expectedError.getMessage() != null) {
            assertThat(actualError.getMessage()).isEqualTo(expectedError.getMessage());
        }

        if (expectedError.getSubErrors() != null) {
            assertThat(actualError.getSubErrors().size()).isEqualTo(expectedError.getSubErrors().size());
            if (!expectedError.getSubErrors().isEmpty()) {
                assertThat(actualError.getSubErrors().get(0).getMessage()).isEqualTo(expectedError.getSubErrors().get(0).getMessage());
                assertThat(actualError.getSubErrors().get(0).getField()).isEqualTo(expectedError.getSubErrors().get(0).getField());
                assertThat(actualError.getSubErrors().get(0).getValue()).isEqualTo(expectedError.getSubErrors().get(0).getValue());
                assertThat(actualError.getSubErrors().get(0).getType()).isEqualTo(expectedError.getSubErrors().get(0).getType());
            }
        }

    }

}