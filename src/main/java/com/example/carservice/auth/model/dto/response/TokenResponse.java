package com.example.carservice.auth.model.dto.response;

import lombok.*;

/**
 * Response DTO representing the result of a successful login or token refresh operation.
 * Contains both access and refresh tokens along with the expiration time of the access token.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private Long accessTokenExpiresAt;
    private String refreshToken;

}
