package com.example.carservice.carservice.model.dto.response;

import com.example.carservice.carservice.model.enums.ServiceStatus;
import lombok.*;

/**
 * Response DTO representing a service returned by the API.
 * Contains service metadata such as title, description, and current status.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {

    private String id;
    private String title;
    private String description;
    private ServiceStatus status;

}
