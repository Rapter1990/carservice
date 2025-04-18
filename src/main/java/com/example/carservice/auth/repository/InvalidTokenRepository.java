package com.example.carservice.auth.repository;

import com.example.carservice.auth.model.entity.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing invalidated JWT tokens.
 * Provides data access methods for {@link InvalidTokenEntity}, such as finding by token ID.
 */
public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {

    /**
     * Finds an {@link InvalidTokenEntity} by its token ID.
     *
     * @param tokenId the unique identifier of the JWT token (jti claim)
     * @return an {@link Optional} containing the {@link InvalidTokenEntity} if found, or empty otherwise
     */
    Optional<InvalidTokenEntity> findByTokenId(final String tokenId);

}
