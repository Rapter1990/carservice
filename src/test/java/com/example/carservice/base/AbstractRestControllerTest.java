package com.example.carservice.base;

import com.example.carservice.auth.config.TokenConfigurationParameter;
import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.model.enums.TokenClaims;
import com.example.carservice.builder.AdminEntityBuilder;
import com.example.carservice.builder.UserEntityBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractRestControllerTest extends AbstractTestContainerConfiguration {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected Token mockUserToken;

    protected Token mockAdminToken;

    @Mock
    private TokenConfigurationParameter tokenConfiguration;

    @BeforeEach
    public void initializeAuth() {

        this.tokenConfiguration = new TokenConfigurationParameter();
        this.mockUserToken = this.generate(new UserEntityBuilder().withValidFields().build().getClaims());
        this.mockAdminToken = this.generate(new AdminEntityBuilder().withValidFields().build().getClaims());
    }

    private Token generate(Map<String, Object> claims) {

        final long currentTimeMillis = System.currentTimeMillis();

        final Date tokenIssuedAt = new Date(currentTimeMillis);

        final Date accessTokenExpiresAt = DateUtils.addMinutes(new Date(currentTimeMillis), tokenConfiguration.getAccessTokenExpireMinute());

        final String accessToken = Jwts.builder()
                .header()
                .add(TokenClaims.TYP.getValue(), OAuth2AccessToken.TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(tokenConfiguration.getIssuer())
                .issuedAt(tokenIssuedAt)
                .expiration(accessTokenExpiresAt)
                .signWith(tokenConfiguration.getPrivateKey())
                .claims(claims)
                .compact();

        final Date refreshTokenExpiresAt = DateUtils.addDays(new Date(currentTimeMillis), tokenConfiguration.getRefreshTokenExpireDay());

        final JwtBuilder refreshTokenBuilder = Jwts.builder();

        final String refreshToken = refreshTokenBuilder
                .header()
                .add(TokenClaims.TYP.getValue(), OAuth2AccessToken.TokenType.BEARER.getValue())
                .and()
                .id(UUID.randomUUID().toString())
                .issuer(tokenConfiguration.getIssuer())
                .issuedAt(tokenIssuedAt)
                .expiration(refreshTokenExpiresAt)
                .signWith(tokenConfiguration.getPrivateKey())
                .claim(TokenClaims.USER_ID.getValue(), claims.get(TokenClaims.USER_ID.getValue()))
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt.toInstant().getEpochSecond())
                .refreshToken(refreshToken)
                .build();

    }

}
