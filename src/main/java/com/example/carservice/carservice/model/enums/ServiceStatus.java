package com.example.carservice.carservice.model.enums;

/**
 * Enumeration representing the lifecycle status of a service operation performed on a car.
 * Useful for tracking and managing the workflow of car services such as maintenance or repairs.
 */
public enum ServiceStatus {

    CREATED,
    PENDING,
    IN_PROGRESS,
    DONE
}
