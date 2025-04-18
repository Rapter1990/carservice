package com.example.carservice.carservice.model;

import com.example.carservice.carservice.model.enums.ServiceStatus;
import com.example.carservice.common.model.BaseDomainModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Data Transfer Object representing a car service entry (e.g., maintenance, repair).
 * Contains basic metadata such as ID, title, description, and service status.
 * Inherits auditing fields from {@link BaseDomainModel}.
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ServiceDto extends BaseDomainModel {

    private String id;
    private String title;
    private String description;
    private ServiceStatus status;

}
