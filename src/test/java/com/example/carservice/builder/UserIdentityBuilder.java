package com.example.carservice.builder;

import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserIdentityBuilder {

    private final TokenService tokenService;

    public String extractUserIdFromToken(String accessToken) {
        return tokenService.getPayload(accessToken)
                .get(TokenClaims.USER_ID.getValue(), String.class);
    }

}