package com.example.carservice.carservice.model.dto.request.services;

import com.example.carservice.carservice.model.enums.ServiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request object used to create a new service entry for a car.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateServiceRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private ServiceStatus status;

}
