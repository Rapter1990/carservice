package com.example.carservice.auth.service;

import com.example.carservice.auth.model.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Map;
import java.util.Set;

/**
 * Service interface for handling JWT token operations such as generation, validation, and parsing.
 */
public interface TokenService {

    /**
     * Generates a new {@link Token} using the provided claims.
     *
     * @param claims the JWT claims to include in the token
     * @return a new {@link Token} containing access and refresh tokens
     */
    Token generateToken(final Map<String, Object> claims);

    /**
     * Generates a new {@link Token} using the provided claims and an existing refresh token.
     *
     * @param claims       the JWT claims to include in the token
     * @param refreshToken the existing refresh token
     * @return a new {@link Token} containing updated access and refresh tokens
     */
    Token generateToken(final Map<String, Object> claims, final String refreshToken);

    /**
     * Extracts Spring Security authentication details from the provided JWT.
     *
     * @param token the JWT token
     * @return an {@link UsernamePasswordAuthenticationToken} containing user credentials and authorities
     */
    UsernamePasswordAuthenticationToken getAuthentication(final String token);

    /**
     * Verifies and validates the integrity and expiration of the provided JWT.
     *
     * @param jwt the JWT to validate
     */
    void verifyAndValidate(final String jwt);

    /**
     * Verifies and validates the integrity and expiration of a set of JWTs.
     *
     * @param jwts the set of JWTs to validate
     */
    void verifyAndValidate(final Set<String> jwts);

    /**
     * Parses the JWT and returns its full claims set.
     *
     * @param jwt the JWT string
     * @return a {@link Jws} containing the JWT claims
     */
    Jws<Claims> getClaims(final String jwt);

    /**
     * Extracts the payload (claims) from the JWT.
     *
     * @param jwt the JWT string
     * @return the JWT {@link Claims} payload
     */
    Claims getPayload(final String jwt);

    /**
     * Retrieves the unique token ID (jti) from the JWT.
     *
     * @param jwt the JWT string
     * @return the token ID as a {@link String}
     */
    String getId(final String jwt);

}
