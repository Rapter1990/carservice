package com.example.carservice.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.example.carservice.auth.config.TokenConfigurationParameter;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.service.InvalidTokenService;
import com.example.carservice.base.AbstractBaseServiceTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.security.*;

import java.util.*;

import static org.mockito.Mockito.*;

class TokenServiceImplTest extends AbstractBaseServiceTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private TokenConfigurationParameter tokenConfigurationParameter;

    @Mock
    private InvalidTokenService invalidTokenService;

    @Test
    void testGenerateTokenWithoutRefreshToken() throws Exception {

        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("USER_ID", "12345");

        // Generate a valid RSA key pair for testing
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        when(tokenConfigurationParameter.getAccessTokenExpireMinute()).thenReturn(60);
        when(tokenConfigurationParameter.getRefreshTokenExpireDay()).thenReturn(7);
        when(tokenConfigurationParameter.getPrivateKey()).thenReturn(privateKey);
        when(tokenConfigurationParameter.getIssuer()).thenReturn("issuer");

        // When
        Token token = tokenService.generateToken(claims);

        assertNotNull(token, "Token should not be null");
        assertNotNull(token.getAccessToken(), "Access token should not be null");
        assertNotNull(token.getRefreshToken(), "Refresh token should not be null");

        // Verify
        verify(tokenConfigurationParameter).getAccessTokenExpireMinute();
        verify(tokenConfigurationParameter).getRefreshTokenExpireDay();
        verify(tokenConfigurationParameter, times(2)).getPrivateKey();

    }

    @Test
    void testGenerateTokenWithRefreshToken() throws NoSuchAlgorithmException {

        // Given
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Use an appropriate key size
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String refreshToken = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .signWith(privateKey) // Use the private key for signing
                .compact();

        Map<String, Object> claims = new HashMap<>();
        claims.put("USER_ID", "12345");

        // Mock the TokenConfigurationParameter to return the keys
        when(tokenConfigurationParameter.getPrivateKey()).thenReturn(privateKey);
        when(tokenConfigurationParameter.getPublicKey()).thenReturn(publicKey);
        when(tokenConfigurationParameter.getAccessTokenExpireMinute()).thenReturn(60);
        when(tokenConfigurationParameter.getIssuer()).thenReturn("issuer");
        doNothing().when(invalidTokenService).checkForInvalidityOfToken(anyString());


        // When
        Token token = tokenService.generateToken(claims, refreshToken);

        // Then
        assertNotNull(token);
        assertNotNull(token.getAccessToken());
        assertNotNull(token.getRefreshToken());
        assertNotNull(token.getAccessTokenExpiresAt());

        // Verify
        verify(tokenConfigurationParameter).getAccessTokenExpireMinute();
        verify(invalidTokenService).checkForInvalidityOfToken(anyString());

    }

    @Test
    void testGetClaims() throws NoSuchAlgorithmException {

        // Given
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Use an appropriate key size
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String jwt = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("issuer")
                .subject("subject")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(privateKey)
                .compact();

        // When
        when(tokenConfigurationParameter.getPublicKey()).thenReturn(publicKey);

        // Then
        Jws<Claims> claims = tokenService.getClaims(jwt);

        assertNotNull(claims, "Claims should not be null");
        assertEquals("issuer", claims.getBody().getIssuer(), "Issuer should match");
        assertEquals("subject", claims.getBody().getSubject(), "Subject should match");

        // Verify
        verify(tokenConfigurationParameter).getPublicKey();

    }

    @Test
    void testGetPayload() throws NoSuchAlgorithmException {

        // Given
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Use an appropriate key size
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String jwt = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("issuer")
                .subject("subject")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(privateKey)
                .compact();

        // When
        when(tokenConfigurationParameter.getPublicKey()).thenReturn(publicKey);

        // Then
        Claims payload = tokenService.getPayload(jwt);


        assertNotNull(payload, "Payload should not be null");
        assertEquals("issuer", payload.getIssuer(), "Issuer should match");
        assertEquals("subject", payload.getSubject(), "Subject should match");

        // Verify
        verify(tokenConfigurationParameter).getPublicKey();

    }

    @Test
    void testVerifyAndValidateSet() throws NoSuchAlgorithmException {

        // Given
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Use an appropriate key size
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String jwt1 = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("issuer1")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(privateKey)
                .compact();

        String jwt2 = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuer("issuer2")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .signWith(privateKey)
                .compact();

        Set<String> jwts = Set.of(jwt1, jwt2);

        // When
        when(tokenConfigurationParameter.getPublicKey()).thenReturn(publicKey);

        // Then
        assertDoesNotThrow(() -> tokenService.verifyAndValidate(jwts), "All tokens should be valid");

        // Verify
        verify(tokenConfigurationParameter, times(2)).getPublicKey();

    }

}