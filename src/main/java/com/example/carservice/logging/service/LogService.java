package com.example.carservice.logging.service;

import com.example.carservice.logging.entity.LogEntity;

/**
 * Service interface for handling log-related operations.
 */
public interface LogService {

    /**
     * Saves the provided {@link LogEntity} to the database.
     *
     * @param logEntity the log entity to persist
     */
    void saveLogToDatabase(final LogEntity logEntity);

}
