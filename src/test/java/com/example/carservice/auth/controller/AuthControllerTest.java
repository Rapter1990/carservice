package com.example.carservice.auth.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.User;
import com.example.carservice.auth.model.dto.request.LoginRequest;
import com.example.carservice.auth.model.dto.request.RegisterRequest;
import com.example.carservice.auth.model.dto.request.TokenInvalidateRequest;
import com.example.carservice.auth.model.dto.request.TokenRefreshRequest;
import com.example.carservice.auth.model.dto.response.TokenResponse;
import com.example.carservice.auth.model.mapper.TokenToTokenResponseMapper;
import com.example.carservice.auth.service.LoginService;
import com.example.carservice.auth.service.LogoutService;
import com.example.carservice.auth.service.RefreshTokenService;
import com.example.carservice.auth.service.RegisterService;
import com.example.carservice.base.AbstractRestControllerTest;
import com.example.carservice.builder.RegisterRequestBuilder;
import com.example.carservice.common.model.dto.response.CustomResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest extends AbstractRestControllerTest {

    @MockitoBean
    RegisterService registerService;

    @MockitoBean
    LoginService loginService;

    @MockitoBean
    RefreshTokenService refreshTokenService;

    @MockitoBean
    LogoutService logoutService;

    private final TokenToTokenResponseMapper tokenToTokenResponseMapper = TokenToTokenResponseMapper.initialize();


    @Test
    void givenValidAdminRegisterRequestWithAdminCreate_whenRegisterAdmin_thenSuccess() throws Exception {

        // Given
        final RegisterRequest request = new RegisterRequestBuilder()
                .withAdminValidFields()
                .build();


        User mockAdminWithCreate = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .build();

        // When
        when(registerService.registerUser(any(RegisterRequest.class))).thenReturn(mockAdminWithCreate);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(registerService, times(1)).registerUser(any(RegisterRequest.class));

    }

    @Test
    void givenValidUserRegisterRequestWithUserCreate_whenRegisterUser_thenSuccess() throws Exception {

        // Given
        final RegisterRequest request = new RegisterRequestBuilder()
                .withUserValidFields()
                .build();


        User mockEmployeeWithCreate = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(request.getUserType())
                .build();

        // When
        when(registerService.registerUser(any(RegisterRequest.class))).thenReturn(mockEmployeeWithCreate);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(registerService, times(1)).registerUser(any(RegisterRequest.class));

    }

    @Test
    void givenLoginRequestWithAdmin_WhenLoginForAdmin_ThenReturnToken() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@example.com")
                .password("password")
                .build();

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = tokenToTokenResponseMapper.map(mockToken);

        // When
        when(loginService.login(any(LoginRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(loginService, times(1)).login(any(LoginRequest.class));

    }

    @Test
    void givenLoginRequestWithUser_WhenLoginForUser_ThenReturnToken() throws Exception {

        // Given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("employee@example.com")
                .password("password")
                .build();

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = tokenToTokenResponseMapper.map(mockToken);

        // When
        when(loginService.login(any(LoginRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(loginService, times(1)).login(any(LoginRequest.class));

    }


    @Test
    void givenTokenRefreshRequestWithAdmin_WhenRefreshTokenForAdmin_ThenReturnTokenResponse() throws Exception {

        // Given
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = tokenToTokenResponseMapper.map(mockToken);

        // When
        when(refreshTokenService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(refreshTokenService, times(1)).refreshToken(any(TokenRefreshRequest.class));

    }

    @Test
    void givenTokenRefreshRequestWithUser_WhenRefreshTokenForUser_ThenReturnTokenResponse() throws Exception {

        // Given
        TokenRefreshRequest tokenRefreshRequest = new TokenRefreshRequest("refreshToken");

        Token mockToken = Token.builder()
                .accessToken("mockAccessToken")
                .accessTokenExpiresAt(3600L)
                .refreshToken("mockRefreshToken")
                .build();

        TokenResponse expectedTokenResponse = tokenToTokenResponseMapper.map(mockToken);

        // When
        when(refreshTokenService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(mockToken);

        // Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.httpStatus").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isSuccess").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessToken").value(expectedTokenResponse.getAccessToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.accessTokenExpiresAt").value(expectedTokenResponse.getAccessTokenExpiresAt()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.refreshToken").value(expectedTokenResponse.getRefreshToken()));

        // Verify
        verify(refreshTokenService, times(1)).refreshToken(any(TokenRefreshRequest.class));

    }

    @Test
    void givenTokenInvalidateRequestWithAdmin_WhenLogoutForAdmin_ThenReturnInvalidateToken() throws Exception {

        // Given
        TokenInvalidateRequest tokenInvalidateRequest = TokenInvalidateRequest.builder()
                .accessToken("Bearer " + mockAdminToken.getAccessToken())
                .refreshToken(mockAdminToken.getRefreshToken())
                .build();

        // When
        doNothing().when(logoutService).logout(any(TokenInvalidateRequest.class));

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenInvalidateRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(logoutService, times(1)).logout(any(TokenInvalidateRequest.class));

    }

    @Test
    void givenTokenInvalidateRequestWithUser_WhenLogoutForUser_ThenReturnInvalidateToken() throws Exception {

        // Given
        TokenInvalidateRequest tokenInvalidateRequest = TokenInvalidateRequest.builder()
                .accessToken("Bearer " + mockUserToken.getAccessToken())
                .refreshToken(mockUserToken.getRefreshToken())
                .build();

        // When
        doNothing().when(logoutService).logout(any(TokenInvalidateRequest.class));

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authentication/user/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenInvalidateRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(CustomResponse.SUCCESS)));

        // Verify
        verify(logoutService, times(1)).logout(any(TokenInvalidateRequest.class));

    }

}