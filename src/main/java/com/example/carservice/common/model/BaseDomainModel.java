package com.example.carservice.common.model;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Abstract base class for domain models that require auditing fields.
 * Contains metadata for creation and update operations, including user and timestamp.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseDomainModel {
    protected String createdUser;
    protected LocalDateTime createdAt;
    protected String updatedUser;
    protected LocalDateTime updatedAt;
}
