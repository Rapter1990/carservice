package com.example.carservice.auth.filter;

import com.example.carservice.auth.model.Token;
import com.example.carservice.auth.service.InvalidTokenService;
import com.example.carservice.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom security filter for handling Bearer token authentication.
 * This filter intercepts HTTP requests, extracts and validates the JWT from the Authorization header,
 * checks if the token has been invalidated (e.g., logged out), and sets the security context accordingly.
 *
 * Applies to every request once per request lifecycle as it extends {@link OncePerRequestFilter}.
 *
 * @see TokenService
 * @see InvalidTokenService
 * @see org.springframework.security.core.context.SecurityContextHolder
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomBearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final InvalidTokenService invalidTokenService;

    /**
     * Intercepts incoming HTTP requests to process JWT-based Bearer authentication.
     * <p>
     * If the Authorization header contains a valid Bearer token, this method:
     * <ul>
     *     <li>Validates the JWT</li>
     *     <li>Checks if the token is invalidated</li>
     *     <li>Builds authentication details and sets them in the SecurityContext</li>
     * </ul>
     * </p>
     *
     * @param httpServletRequest  the incoming HTTP request
     * @param httpServletResponse the response object
     * @param filterChain         the remaining filter chain
     * @throws ServletException if filter chain processing fails
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull final HttpServletRequest httpServletRequest,
                                    @NonNull final HttpServletResponse httpServletResponse,
                                    @NonNull final FilterChain filterChain) throws ServletException, IOException {

        log.debug("API Request was secured with Security!");

        final String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (Token.isBearerToken(authorizationHeader)) {

            final String jwt = Token.getJwt(authorizationHeader);

            tokenService.verifyAndValidate(jwt);

            final String tokenId = tokenService.getId(jwt);

            invalidTokenService.checkForInvalidityOfToken(tokenId);

            final UsernamePasswordAuthenticationToken authentication = tokenService
                    .getAuthentication(jwt);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(httpServletRequest,httpServletResponse);

    }

}
