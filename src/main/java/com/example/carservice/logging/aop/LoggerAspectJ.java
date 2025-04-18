package com.example.carservice.logging.aop;

import com.example.carservice.auth.exception.*;
import com.example.carservice.carservice.exception.*;
import com.example.carservice.logging.entity.LogEntity;
import com.example.carservice.logging.service.LogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Optional;

/**
 * Aspect for logging REST controller activity including successful responses and thrown exceptions.
 * Captures metadata such as request endpoint, method, user, status, and error messages,
 * and logs it into the database using {@link LogService}.
 * This aspect applies to all classes annotated with {@code @RestController}.
 *
 * @see LogEntity
 * @see LogService
 */
@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerAspectJ {

    private final LogService logService;

    /**
     * Pointcut expression targeting all classes annotated with {@code @RestController}.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {

    }

    /**
     * Advice that logs exceptions thrown from any method within a {@code @RestController}.
     * Builds a {@link LogEntity} and stores it using {@link LogService}.
     *
     * @param joinPoint the join point representing the method that threw the exception
     * @param ex        the exception that was thrown
     */
    @AfterThrowing(pointcut = "restControllerPointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {

        Optional<ServletRequestAttributes> requestAttributes = Optional.ofNullable(
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        );

        if (requestAttributes.isPresent()) {

            final HttpServletRequest request = requestAttributes.get().getRequest();

            LogEntity logEntity = LogEntity.builder()
                    .endpoint(request.getRequestURL().toString())
                    .method(request.getMethod())
                    .message(ex.getMessage())
                    .errorType(ex.getClass().getName())
                    .status(HttpStatus.valueOf(getHttpStatusFromException(ex)))
                    .operation(joinPoint.getSignature().getName())
                    .response(ex.getMessage())
                    .build();

            // Get the username from SecurityContextHolder and set it in logEntity
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                String username = authentication.getName();
                logEntity.setUserInfo(username);
            }

            try {
                logService.saveLogToDatabase(logEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.error("Request Attributes are null!");
        }

    }

    /**
     * Advice that logs successful executions of controller methods.
     * Builds and stores a {@link LogEntity} after the method returns.
     *
     * @param joinPoint the join point representing the executed controller method
     * @param result    the result returned from the method
     * @throws IOException if JSON serialization fails
     */
    @AfterReturning(value = "restControllerPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) throws IOException {

        Optional<ServletRequestAttributes> requestAttributes = Optional.ofNullable(
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        );

        if (requestAttributes.isPresent()) {

            final HttpServletRequest request = requestAttributes.get().getRequest();
            final HttpServletResponse response = requestAttributes.get().getResponse();

            String responseObject = "";

            LogEntity logEntity = LogEntity.builder()
                    .endpoint(request.getRequestURL().toString())
                    .method(request.getMethod())
                    .operation(joinPoint.getSignature().getName())
                    .build();

            if (result instanceof JsonNode) {
                ObjectMapper objectMapper = new ObjectMapper();
                responseObject = objectMapper.writeValueAsString(result);
            } else {
                responseObject = result.toString();
            }

            logEntity.setResponse(responseObject);
            logEntity.setMessage(responseObject);
            Optional.ofNullable(response).ifPresent(
                    httpServletResponse -> logEntity.setStatus(
                            HttpStatus.valueOf(httpServletResponse.getStatus()
                            )
                    ));

            try {
                logService.saveLogToDatabase(logEntity);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        } else {
            log.error("Request Attributes are null!");
        }
    }

    /**
     * Resolves the appropriate {@link HttpStatus} for a given exception type.
     *
     * @param ex the thrown exception
     * @return the name of the corresponding HTTP status
     */
    private String getHttpStatusFromException(Exception ex) {
        return switch (ex.getClass().getSimpleName()) {
            case "PasswordNotValidException" -> PasswordNotValidException.STATUS.name();
            case "RoleNotFoundException" -> RoleNotFoundException.STATUS.name();
            case "TokenAlreadyInvalidatedException" -> TokenAlreadyInvalidatedException.STATUS.name();
            case "UserAlreadyExistException" -> UserAlreadyExistException.STATUS.name();
            case "UserNotFoundException" -> UserNotFoundException.STATUS.name();
            case "UserStatusNotValidException" -> UserStatusNotValidException.STATUS.name();
            case "CarNotFoundException" -> CarNotFoundException.STATUS.name();
            case "CarStatusNotValidException" -> CarStatusNotValidException.STATUS.name();
            case "LicensePlateAlreadyExistsException" -> LicensePlateAlreadyExistsException.STATUS.name();
            case "ServiceCarMismatchException" -> ServiceCarMismatchException.STATUS.name();
            case "ServiceNotFoundException" -> ServiceNotFoundException.STATUS.name();
            case "ServiceTitleAlreadyExistsException" -> ServiceTitleAlreadyExistsException.STATUS.name();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.name();
        };
    }

}
