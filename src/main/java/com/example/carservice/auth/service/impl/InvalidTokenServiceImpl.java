package com.example.carservice.auth.service.impl;

import com.example.carservice.auth.exception.TokenAlreadyInvalidatedException;
import com.example.carservice.auth.model.entity.InvalidTokenEntity;
import com.example.carservice.auth.repository.InvalidTokenRepository;
import com.example.carservice.auth.service.InvalidTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for managing invalid or revoked JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class InvalidTokenServiceImpl implements InvalidTokenService {

    private final InvalidTokenRepository invalidTokenRepository;

    /**
     * Invalidates a set of JWT token IDs.
     *
     * @param tokenIds the set of token IDs to invalidate
     */
    @Override
    public void invalidateTokens(Set<String> tokenIds) {

        final Set<InvalidTokenEntity> invalidTokenEntities = tokenIds.stream()
                .map(tokenId -> InvalidTokenEntity.builder()
                        .tokenId(tokenId)
                        .build()
                )
                .collect(Collectors.toSet());

        invalidTokenRepository.saveAll(invalidTokenEntities);
    }

    /**
     * Checks whether a given token ID has been invalidated.
     *
     * @param tokenId the token ID to check
     */
    @Override
    public void checkForInvalidityOfToken(String tokenId) {

        final boolean isTokenInvalid = invalidTokenRepository.findByTokenId(tokenId).isPresent();

        if (isTokenInvalid) {
            throw new TokenAlreadyInvalidatedException(tokenId);
        }

    }

}
