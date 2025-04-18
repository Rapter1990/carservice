package com.example.carservice.carservice.model.dto.request.car;

import com.example.carservice.carservice.utils.annotation.ValidTurkishPlate;
import lombok.*;

/**
 * Request object used to create a new car.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarRequest {

    @ValidTurkishPlate
    private String licensePlate;
    private String model;
    private String brand;
    private String userId;

}
