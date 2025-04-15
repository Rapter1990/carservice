package com.example.carservice.builder;

import com.example.carservice.auth.model.enums.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenBuilder {

    public static Claims getValidClaims(String userId, String firstName) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put(TokenClaims.JWT_ID.getValue(), UUID.randomUUID().toString());
        claimsMap.put(TokenClaims.USER_ID.getValue(), userId);
        claimsMap.put(TokenClaims.USER_FIRST_NAME.getValue(), firstName);
        return new DefaultClaims(claimsMap);
    }

}
