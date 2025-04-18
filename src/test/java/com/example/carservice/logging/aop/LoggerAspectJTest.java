package com.example.carservice.logging.aop;

import ch.qos.logback.classic.Level;
import com.example.carservice.auth.exception.*;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.carservice.exception.*;
import com.example.carservice.logging.entity.LogEntity;
import com.example.carservice.logging.service.LogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggerAspectJTest extends AbstractBaseServiceTest {

    @InjectMocks
    private LoggerAspectJ loggerAspectJ;

    @Mock
    private LogService logService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and set request attributes
        when(servletRequestAttributes.getRequest()).thenReturn(httpServletRequest);
        when(servletRequestAttributes.getResponse()).thenReturn(httpServletResponse);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);

        // Mock JoinPoint signature
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(signature.getDeclaringTypeName()).thenReturn("LoggerAspectJ");
        when(signature.getDeclaringType()).thenReturn(LoggerAspectJ.class);
    }

    @Test
    public void testLogAfterThrowing() {

        // Given
        Exception ex = new UserNotFoundException("User not found");

        // When
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("GET");

        // Set an authenticated user in the security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);

        // Then
        loggerAspectJ.logAfterThrowing(joinPoint, ex);

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterThrowing_AnonymousUser() {

        // Given
        Exception ex = new UserNotFoundException("User not found");
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ANONYMOUS");
        List<GrantedAuthority> authorities = Collections.singletonList(authority);
        SecurityContextHolder.setContext(securityContext);

        // When
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(securityContext.getAuthentication()).thenReturn(new AnonymousAuthenticationToken("key", "anonymousUser", authorities));

        // Then
        loggerAspectJ.logAfterThrowing(joinPoint, ex);

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterReturning() throws IOException {

        // When
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletResponse.getStatus()).thenReturn(HttpStatus.OK.value());
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getSignature()).thenReturn(signature);

        // Then
        loggerAspectJ.logAfterReturning(joinPoint, "test response");

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterReturning_WithJsonNode() throws IOException {

        // Given
        JsonNode jsonNode = new ObjectMapper().createObjectNode().put("key", "value");

        // When
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletResponse.getStatus()).thenReturn(HttpStatus.OK.value());

        // Then
        loggerAspectJ.logAfterReturning(joinPoint, jsonNode);

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterReturning_NoResponseAttributes() throws IOException {

        // Given
        RequestContextHolder.resetRequestAttributes();

        // When
        loggerAspectJ.logAfterReturning(mock(JoinPoint.class), "test response");

        // Then
        verify(logService, never()).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterThrowing_LogServiceException() {

        // Given
        Exception ex = new UserNotFoundException("User not found");

        // When
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("GET");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContextHolder.setContext(securityContext);
        doThrow(new RuntimeException("Database connection error")).when(logService).saveLogToDatabase(any(LogEntity.class));

        // Then
        loggerAspectJ.logAfterThrowing(joinPoint, ex);

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    public void testLogAfterReturning_SaveLogThrowsException() throws IOException {

        // Given
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletResponse.getStatus()).thenReturn(HttpStatus.OK.value());
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getSignature()).thenReturn(signature);

        // When
        doThrow(new RuntimeException("Database error")).when(logService).saveLogToDatabase(any(LogEntity.class));

        // Then
        assertDoesNotThrow(() -> loggerAspectJ.logAfterReturning(joinPoint, "test response"));

        // Verify
        verify(logService, times(1)).saveLogToDatabase(any(LogEntity.class));

    }

    @Test
    void testLogAfterThrowing_whenRequestAttributesAreNull_thenLogError() {
        // Given
        RequestContextHolder.resetRequestAttributes();
        Exception ex = new RuntimeException("Some error");

        // When
        loggerAspectJ.logAfterThrowing(joinPoint, ex);

        // Then
        Optional<String> logMessage = logTracker.checkMessage(Level.ERROR, "Request Attributes are null!");
        assertTrue(logMessage.isPresent(), "Expected error log message not found.");
    }

    @Test
    public void testGetHttpStatusFromException_AllCases() {
        // Given a mapping between exception instances and their expected HTTP status values
        Map<Exception, String> testCases = new HashMap<>();
        testCases.put(new PasswordNotValidException("Invalid password"), PasswordNotValidException.STATUS.name());
        testCases.put(new RoleNotFoundException("Role not found"), RoleNotFoundException.STATUS.name());
        testCases.put(new TokenAlreadyInvalidatedException("Token already invalidated"), TokenAlreadyInvalidatedException.STATUS.name());
        testCases.put(new UserAlreadyExistException("User already exists"), UserAlreadyExistException.STATUS.name());
        testCases.put(new UserNotFoundException("User not found"), UserNotFoundException.STATUS.name());
        testCases.put(new UserStatusNotValidException("User status not valid"), UserStatusNotValidException.STATUS.name());
        testCases.put(new CarNotFoundException("Car Not Found"), CarNotFoundException.STATUS.name());
        testCases.put(new CarStatusNotValidException("Invalid car status"), CarStatusNotValidException.STATUS.name());
        testCases.put(new LicensePlateAlreadyExistsException("License plate already exists"), LicensePlateAlreadyExistsException.STATUS.name());
        testCases.put(new ServiceCarMismatchException("CarId","ServiceId"), ServiceCarMismatchException.STATUS.name());
        testCases.put(new ServiceNotFoundException("Service Not Found"), ServiceNotFoundException.STATUS.name());
        testCases.put(new ServiceTitleAlreadyExistsException("Service Title already exists"), ServiceTitleAlreadyExistsException.STATUS.name());
        testCases.put(new Exception("Unknown exception"), HttpStatus.INTERNAL_SERVER_ERROR.name());

        // When & Then: using ReflectionTestUtils to call the private method getHttpStatusFromException
        testCases.forEach((exception, expectedStatus) -> {
            String actualStatus = (String) ReflectionTestUtils.invokeMethod(
                    loggerAspectJ,
                    "getHttpStatusFromException",
                    exception
            );
            assertEquals(expectedStatus, actualStatus,
                    "Failed for exception: " + exception.getClass().getSimpleName());
        });
    }

}