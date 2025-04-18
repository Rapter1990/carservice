package com.example.carservice.auth.service;

import java.util.Set;

/**
 * Service interface for managing invalid or revoked JWT tokens.
 */
public interface InvalidTokenService {

    /**
     * Invalidates a set of JWT token IDs.
     *
     * @param tokenIds the set of token IDs to invalidate
     */
    void invalidateTokens(final Set<String> tokenIds);

    /**
     * Checks whether a given token ID has been invalidated.
     *
     * @param tokenId the token ID to check
     */
    void checkForInvalidityOfToken(final String tokenId);

}
