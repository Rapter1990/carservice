package com.example.carservice.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.carservice.auth.exception.UserNotFoundException;
import com.example.carservice.auth.exception.UserStatusNotValidException;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.dto.request.TokenRefreshRequest;
import com.example.carservice.auth.model.entity.UserEntity;
import com.example.carservice.auth.model.enums.UserStatus;
import com.example.carservice.auth.repository.UserRepository;
import com.example.carservice.auth.service.TokenService;
import com.example.carservice.base.AbstractBaseServiceTest;
import com.example.carservice.builder.AdminEntityBuilder;
import com.example.carservice.builder.TokenBuilder;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefreshTokenServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Test
    void refreshToken_ValidRefreshToken_ReturnsToken() {

        // Given
        final String refreshTokenString = "mockRefreshToken";
        final TokenRefreshRequest tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshTokenString)
                .build();

        final UserEntity mockAdminUserEntity = new AdminEntityBuilder().withValidFields().build();

        final Claims mockClaims = TokenBuilder.getValidClaims(
                mockAdminUserEntity.getId(),
                mockAdminUserEntity.getFirstName()
        );

        final Token expectedToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(123456789L)
                .refreshToken("newMockRefreshToken")
                .build();

        doNothing().when(tokenService).verifyAndValidate(refreshTokenString);
        when(tokenService.getPayload(refreshTokenString)).thenReturn(mockClaims);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(mockAdminUserEntity));
        when(tokenService.generateToken(mockAdminUserEntity.getClaims(), refreshTokenString)).thenReturn(expectedToken);

        // When
        Token actualToken = refreshTokenService.refreshToken(tokenRefreshRequest);

        // Then
        assertNotNull(actualToken);
        assertEquals(expectedToken.getAccessToken(), actualToken.getAccessToken());
        assertEquals(expectedToken.getAccessTokenExpiresAt(), actualToken.getAccessTokenExpiresAt());
        assertEquals(expectedToken.getRefreshToken(), actualToken.getRefreshToken());

        // Verify
        verify(tokenService).verifyAndValidate(refreshTokenString);
        verify(tokenService).getPayload(refreshTokenString);
        verify(userRepository).findById(anyString());
        verify(tokenService).generateToken(mockAdminUserEntity.getClaims(), refreshTokenString);

    }

    @Test
    void refreshToken_InvalidRefreshToken_ThrowsException() {

        // Given
        final String refreshTokenString = "invalidRefreshToken";
        final TokenRefreshRequest tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshTokenString)
                .build();

        // When
        doThrow(RuntimeException.class).when(tokenService).verifyAndValidate(refreshTokenString);

        // Then
        assertThrows(RuntimeException.class,
                () -> refreshTokenService.refreshToken(tokenRefreshRequest));

        // Verify
        verify(tokenService).verifyAndValidate(refreshTokenString);
        verifyNoInteractions(userRepository);

    }

    @Test
    void refreshToken_AdminNotFound_ThrowsException() {

        // Given
        final String refreshTokenString = "validRefreshToken";
        final TokenRefreshRequest tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshTokenString)
                .build();

        final Claims mockClaims = TokenBuilder.getValidClaims("nonExistentAdminId", "John");

        // When
        doNothing().when(tokenService).verifyAndValidate(refreshTokenString);
        when(tokenService.getPayload(refreshTokenString)).thenReturn(mockClaims);
        when(userRepository.findById("nonExistentAdminId")).thenReturn(Optional.empty());

        // Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> refreshTokenService.refreshToken(tokenRefreshRequest));

        assertEquals("""
            User not found!
            """, exception.getMessage());

        // Verify
        verify(tokenService).verifyAndValidate(refreshTokenString);
        verify(tokenService).getPayload(refreshTokenString);
        verify(userRepository).findById("nonExistentAdminId");

    }

    @Test
    void refreshToken_InactiveAdmin_ThrowsException() {

        // Given
        String refreshTokenString = "validRefreshToken";
        TokenRefreshRequest tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(refreshTokenString)
                .build();

        UserEntity inactiveAdmin = new AdminEntityBuilder().withValidFields().withUserStatus(UserStatus.PASSIVE).build();

        Claims mockClaims = TokenBuilder.getValidClaims(inactiveAdmin.getId(), inactiveAdmin.getFirstName());

        // When
        doNothing().when(tokenService).verifyAndValidate(refreshTokenString);
        when(tokenService.getPayload(refreshTokenString)).thenReturn(mockClaims);
        when(userRepository.findById(inactiveAdmin.getId())).thenReturn(Optional.of(inactiveAdmin));

        // Then
        UserStatusNotValidException exception = assertThrows(UserStatusNotValidException.class,
                () -> refreshTokenService.refreshToken(tokenRefreshRequest));

        assertEquals("User status is not valid!\n UserStatus = PASSIVE", exception.getMessage());

        // Verify
        verify(tokenService).verifyAndValidate(refreshTokenString);
        verify(tokenService).getPayload(refreshTokenString);
        verify(userRepository).findById(inactiveAdmin.getId());

    }

}