package com.example.carservice.carservice.model.dto.request.services;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignServiceToCarRequest {

    @NotBlank
    private String carId;

    @NotBlank
    private String serviceId;

}
