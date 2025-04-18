package com.example.carservice.carservice.model.dto.request.services;

import com.example.carservice.carservice.model.enums.ServiceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateServiceRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private ServiceStatus status;

}
