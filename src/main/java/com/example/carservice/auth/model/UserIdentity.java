package com.example.carservice.auth.model;

import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.common.model.BeanScope;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@Scope(value = BeanScope.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserIdentity {

    /**
     * Returns the user ID associated with the authenticated user's AYS identity.
     * The user ID is extracted from the JWT claims.
     *
     * @return the user ID as a {@link String}
     */
    public String getUserId() {
        return this.getJwt().getClaim(TokenClaims.USER_ID.getValue());
    }

    public UserType getUserType() {
        String userType = this.getJwt().getClaim(TokenClaims.USER_TYPE.getValue());
        return UserType.valueOf(userType); // Assumes "USER" or "ADMIN"
    }

    /**
     * Retrieves the JWT token for the authenticated user from the security context.
     * This method is used internally to access user-specific claims from the JWT.
     *
     * @return the JWT token as a {@link Jwt} object
     */
    private Jwt getJwt() {
        return ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

}
