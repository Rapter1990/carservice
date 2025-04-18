package com.example.carservice.auth.repository;

import com.example.carservice.auth.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for user data persistence and retrieval.
 * Provides custom query methods for operations based on user email.
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Checks whether a user exists with the given email address.
     *
     * @param email the email address to check
     * @return {@code true} if a user with the given email exists; {@code false} otherwise
     */
    boolean existsUserEntityByEmail(final String email);

    /**
     * Retrieves a user entity by their email address.
     *
     * @param email the email of the user to retrieve
     * @return an {@link Optional} containing the {@link UserEntity} if found, or empty otherwise
     */
    Optional<UserEntity> findUserEntityByEmail(final String email);

}
