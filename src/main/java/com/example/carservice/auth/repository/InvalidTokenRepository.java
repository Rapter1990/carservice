package com.example.carservice.auth.repository;

import com.example.carservice.auth.model.entity.InvalidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidTokenRepository extends JpaRepository<InvalidTokenEntity, String> {

    Optional<InvalidTokenEntity> findByTokenId(final String tokenId);

}
