package com.example.carservice.auth.service.impl;

import com.example.carservice.auth.config.TokenConfigurationParameter;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.enums.ConfigurationParameter;
import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.auth.model.enums.TokenType;
import com.example.carservice.auth.model.enums.UserType;
import com.example.carservice.auth.service.InvalidTokenService;
import com.example.carservice.auth.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service implementation for handling JWT token operations such as generation, validation, and parsing.
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenConfigurationParameter tokenConfigurationParameter;
    private final InvalidTokenService invalidTokenService;

    /**
     * Generates a new {@link Token} using the provided claims.
     *
     * @param claims the JWT claims to include in the token
     * @return a new {@link Token} containing access and refresh tokens
     */
    @Override
    public Token generateToken(Map<String, Object> claims) {

        final long currentTimeMillis = System.currentTimeMillis();

        final Date tokenIssuedAt = new Date(currentTimeMillis);

        final Date accessTokenExpiresAt = DateUtils.addMinutes(
                new Date(currentTimeMillis),
                tokenConfigurationParameter.getAccessTokenExpireMinute()
        );

        final String accessToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(ConfigurationParameter.ISSUER.getDefaultValue())
                .issuedAt(tokenIssuedAt)
                .expiration(accessTokenExpiresAt)
                .signWith(tokenConfigurationParameter.getPrivateKey())
                .claims(claims)
                .compact();

        final Date refreshTokenExpiresAt = DateUtils.addDays(
                new Date(currentTimeMillis),
                tokenConfigurationParameter.getRefreshTokenExpireDay()
        );

        final String refreshToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(tokenConfigurationParameter.getIssuer())
                .issuedAt(tokenIssuedAt)
                .expiration(refreshTokenExpiresAt)
                .signWith(tokenConfigurationParameter.getPrivateKey())
                .claim(TokenClaims.USER_ID.getValue(), claims.get(TokenClaims.USER_ID.getValue()))
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Generates a new {@link Token} using the provided claims and an existing refresh token.
     *
     * @param claims       the JWT claims to include in the token
     * @param refreshToken the existing refresh token
     * @return a new {@link Token} containing updated access and refresh tokens
     */
    @Override
    public Token generateToken(Map<String, Object> claims, String refreshToken) {

        final long currentTimeMillis = System.currentTimeMillis();

        final String refreshTokenId = this.getId(refreshToken);

        invalidTokenService.checkForInvalidityOfToken(refreshTokenId);

        final Date accessTokenIssuedAt = new Date(currentTimeMillis);

        final Date accessTokenExpiresAt = DateUtils.addMinutes(
                new Date(currentTimeMillis),
                tokenConfigurationParameter.getAccessTokenExpireMinute()
        );

        final String accessToken = Jwts.builder()
                .header()
                .type(TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(tokenConfigurationParameter.getIssuer())
                .issuedAt(accessTokenIssuedAt)
                .expiration(accessTokenExpiresAt)
                .signWith(tokenConfigurationParameter.getPrivateKey())
                .claims(claims)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Extracts Spring Security authentication details from the provided JWT.
     *
     * @param token the JWT token
     * @return an {@link UsernamePasswordAuthenticationToken} containing user credentials and authorities
     */
    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        final Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(tokenConfigurationParameter.getPublicKey())
                .build()
                .parseSignedClaims(token);

        final JwsHeader jwsHeader = claimsJws.getHeader();
        final Claims payload = claimsJws.getPayload();

        final Jwt jwt = new org.springframework.security.oauth2.jwt.Jwt(
                token,
                payload.getIssuedAt().toInstant(),
                payload.getExpiration().toInstant(),
                Map.of(
                        TokenClaims.TYP.getValue(), jwsHeader.getType(),
                        TokenClaims.ALGORITHM.getValue(), jwsHeader.getAlgorithm()
                ),
                payload
        );


        final UserType userType = UserType.valueOf(payload.get(TokenClaims.USER_TYPE.getValue()).toString());

        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userType.name()));

        return UsernamePasswordAuthenticationToken
                .authenticated(jwt, null, authorities);
    }

    /**
     * Verifies and validates the integrity and expiration of the provided JWT.
     *
     * @param jwt the JWT to validate
     */
    @Override
    public void verifyAndValidate(String jwt) {
        Jwts.parser()
                .verifyWith(tokenConfigurationParameter.getPublicKey())
                .build()
                .parseSignedClaims(jwt);
    }

    /**
     * Verifies and validates the integrity and expiration of a set of JWTs.
     *
     * @param jwts the set of JWTs to validate
     */
    @Override
    public void verifyAndValidate(Set<String> jwts) {
        jwts.forEach(this::verifyAndValidate);
    }

    /**
     * Parses the JWT and returns its full claims set.
     *
     * @param jwt the JWT string
     * @return a {@link Jws} containing the JWT claims
     */
    @Override
    public Jws<Claims> getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfigurationParameter.getPublicKey())
                .build()
                .parseSignedClaims(jwt);
    }

    /**
     * Extracts the payload (claims) from the JWT.
     *
     * @param jwt the JWT string
     * @return the JWT {@link Claims} payload
     */
    @Override
    public Claims getPayload(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfigurationParameter.getPublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    /**
     * Retrieves the unique token ID (jti) from the JWT.
     *
     * @param jwt the JWT string
     * @return the token ID as a {@link String}
     */
    @Override
    public String getId(String jwt) {
        return Jwts.parser()
                .verifyWith(tokenConfigurationParameter.getPublicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getId();
    }

}
