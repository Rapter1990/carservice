package com.example.carservice.auth.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * Represents a JWT-based authentication token containing access and refresh tokens.
 */
@Getter
@Builder
public class Token {

    private String accessToken;
    private Long accessTokenExpiresAt;
    private String refreshToken;

    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Checks if the provided authorization header contains a Bearer token.
     *
     * @param authorizationHeader the value of the Authorization header
     * @return {@code true} if the header starts with "Bearer "; otherwise {@code false}
     */
    public static boolean isBearerToken(final String authorizationHeader) {
        return StringUtils.hasText(authorizationHeader) &&
                authorizationHeader.startsWith(TOKEN_PREFIX);
    }

    /**
     * Extracts the raw JWT from a Bearer authorization header.
     *
     * @param authorizationHeader the value of the Authorization header
     * @return the JWT string without the "Bearer " prefix
     */
    public static String getJwt(final String authorizationHeader) {
        return authorizationHeader.replace(TOKEN_PREFIX, "");
    }

}
